package sepm.dsa.service;

import org.apache.commons.collections.IteratorUtils;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.dao.MovingTraderDao;
import sepm.dsa.dao.OfferDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.path.NoPathException;
import sepm.dsa.service.path.PathService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.*;
import java.util.*;

public class TraderServiceImpl implements TraderService {
    private static final Logger log = LoggerFactory.getLogger(TraderServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TraderDao traderDao;
	private MovingTraderDao movingTraderDao;
    private ProductService productService;
    private PathService<RegionBorder> pathService;
    private RegionService regionService;
    private RegionBorderService regionBorderService;
    private TimeService timeService;
    private DealService dealService;
    private CurrencyService currencyService;
    private OfferDao offerDao;

    private static final Double EPSILON = 1E-5;

	private static final File nameFile = new File("resources/nameFile.txt");

	@Override
    public void addManualOffer(Trader trader, Offer offer) {
        log.debug("calling addManualOffer(" + trader + ", " + offer + ")");
        Set<Offer> offers = trader.getOffers();
        Offer raiseOffer = null;
        for (Offer o : offers){
            if (offer.getProduct().equals(o.getProduct())){
                if (offer.getQuality().equals(o.getQuality())){
                    raiseOffer=o;
                    break;
                }
            }
        }
        if (raiseOffer!=null){
            raiseOffer.setAmount(raiseOffer.getAmount()+offer.getAmount());
        }else {
            offerDao.add(offer);
            offers.add(offer);
        }
        trader.setOffers(offers);
    }

    @Override
    public void removeManualOffer(Trader trader, Offer offer, double amount) {
        log.debug("calling removeManualOffer(" + trader + ", " + offer + ", " + amount + ")");
        if (amount >= offer.getAmount()) {
            offerDao.remove(offer);
            Set<Offer> offers = trader.getOffers();
            offers.remove(offer);
            trader.setOffers(offers);
        }else {
            offer.setAmount(offer.getAmount()-amount);
            offerDao.update(offer);
        }
        traderDao.update(trader);
    }

	@Override
    public Trader get(int id) {
        log.debug("calling get(" + id + ")");
        Trader result = traderDao.get(id);
		if (result instanceof MovingTrader) {
			result = movingTraderDao.get(id);
		}
        log.trace("returning " + result);
        return result;
    }

    @Transactional(readOnly = false)
    @Override
    public void makeTraderToMovingTrader(MovingTrader trader) {
        log.debug("calling makeTraderToMovingTrader(" + trader + ")");
        movingTraderDao.addMovingToTrader(trader);
    }

    /**
     * Adds a new trader and generate and save a set of offers for him
     * @param t (Trader) to be persisted must not be null
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public Trader add(Trader t) {
        log.debug("calling addTrader(" + t + ")");
        validate(t);
	    Trader trader;
	    if (t instanceof MovingTrader) {
            log.info("movingTraderDao.add((MovingTrader) " + t + ")");
		    trader = movingTraderDao.add((MovingTrader) t);
	    } else {
            log.info("traderDao.add((MovingTrader) " + t + ")");
            trader = traderDao.add(t);
	    }
		List<Offer> offers = calculateOffers(t);
        offers.forEach(this::validateOffer);
        offerDao.addList(offers);
        trader.setOffers(new HashSet<>(offers));

        return trader;
    }

    @Override
    @Transactional(readOnly = false)
    public Trader update(Trader t) {
        log.debug("calling update(" + t + ")");
        validate(t);
	    if (t instanceof MovingTrader) {
            log.info("movingTraderDao.update((MovingTrader) " + t + ")");
            return movingTraderDao.update((MovingTrader) t);
	    }
        log.info("traderDao.update((MovingTrader) " + t + ")");
        return traderDao.update(t);
    }

    @Override
    public void makeMovingTraderToTrader(Trader trader) {
        log.debug("calling makeMovingTraderToTrader(" + trader + ")");
        MovingTrader mt = new MovingTrader();
        mt.setId(trader.getId());
        movingTraderDao.removeMovingFromMovingTrader(mt);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Trader t) {
        log.debug("calling removeConnection(" + t + ")");
	    if (t instanceof MovingTrader) {
		    movingTraderDao.remove((MovingTrader) t);
	    }
        traderDao.remove(t);
    }

    @Override
    public List<Trader> getAll() {
        log.debug("calling getAll()");
	    List<Trader> traders = traderDao.getAll();
	    List<MovingTrader> movingTraders = movingTraderDao.getAll();
	    List<Trader> result = new ArrayList<>();
	    for (Trader t : traders) {
		    if (!(t instanceof MovingTrader)) {
			    result.add(t);
		    }
	    }
	    result.addAll(movingTraders);
	    log.trace("returning " + result);
	    return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Trader recalculateOffers(Trader t) {
        log.debug("calling addConnection(" + t + ")");
        Set<Offer> oldOffers = t.getOffers();
        Offer[] newOfferList = new Offer[oldOffers.size()];

        int i = 0;
        for(Offer o : oldOffers){
            newOfferList[i] = o;
            i++;
        }
        for(Offer o: newOfferList){
            offerDao.remove(o);
        }

        List<Offer> offers = calculateOffers(t);
        offerDao.addList(offers);
        t.setOffers(new HashSet<>(offers));

        return t;
    }

    @Override
    public List<MovingTrader> getAllMovingTraders() {
        log.debug("calling getAllMovingTraders()");
	    return movingTraderDao.getAll();
    }

    @Override
    public List<Trader> getAllForLocation(Location location) {
        log.debug("calling getAllForLocation()");
        List<Trader> traders = traderDao.getAllByLocation(location);
	    List<MovingTrader> movingTraders = movingTraderDao.getAllByLocation(location);
	    List<Trader> result = new ArrayList<>();
	    for (Trader t : traders) {
		    if (!(t instanceof MovingTrader)) {
			    result.add(t);
		    }
	    }
	    result.addAll(movingTraders);
	    log.trace("returning " + result);
	    return result;
    }

    @Override
    public List<Trader> getAllByCategory(TraderCategory traderCategory) {
        log.debug("calling getAllByCategory()");
	    List<Trader> traders = traderDao.getAllByCategory(traderCategory);
	    List<MovingTrader> movingTraders = movingTraderDao.getAllByCategory(traderCategory);
	    List<Trader> result = new ArrayList<>();
	    for (Trader t : traders) {
		    if (!(t instanceof MovingTrader)) {
			    result.add(t);
		    }
	    }
	    result.addAll(movingTraders);
	    log.trace("returning " + result);
	    return result;
    }

    @Override
    public Deal sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, List<CurrencyAmount> totalPrice, Integer discount, boolean removeRemainingOfferAmount) {
        Integer baseValuePrice = currencyService.exchangeToBaseRate(totalPrice);
        return sellToPlayer(trader, player, product, productQuality, unit, amount, baseValuePrice, discount, removeRemainingOfferAmount);
    }

    @Override
    public Deal buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, List<CurrencyAmount> totalPrice) {
        Integer baseValuePrice = currencyService.exchangeToBaseRate(totalPrice);
        return buyFromPlayer(trader, player, product, productQuality, unit, amount, baseValuePrice);
    }

    @Override
    public Deal sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer productAmount, Integer totalPrice, Integer discount, boolean removeRemainingOfferAmount) {
        log.debug("calling sellToPlayer(" + trader + ", " + player + ", " + product + ", " + productQuality + ", " + unit + ", " + productAmount + ", " + totalPrice + ", " + discount + ", " + removeRemainingOfferAmount + ")");
        Offer offer = null;
        Set<Offer> traderOffers = trader.getOffers();
        for (Offer o : traderOffers) {
            if (o.getProduct().equals(product) && o.getQuality().equals(productQuality)) {
                offer = o;
                break;
            }
        }
        if (offer == null) {
            throw new DSAValidationException("Der Händler hat die Ware in der gewünschten Qualität nicht.");
        }

        Double offerAmountDifference = unit.exchange(productAmount.doubleValue(), product.getUnit());
        Double remainingAmount = offer.getAmount() - offerAmountDifference;
        log.debug("remaing amount would be " + remainingAmount);
        if (remainingAmount < 0) {
            throw new DSAValidationException("Der Händler hat nicht genug Waren dieser Art in dieser Qualitätsstufe");
        }

        if (totalPrice.doubleValue() < 0) {
            throw new DSAValidationException("Der Preis darf nicht negativ sein");
        }

        if (!unit.getUnitType().equals(offer.getProduct().getUnit().getUnitType())) {
            throw new DSAValidationException("Einheit der Ware " + product.getUnit().getUnitType().getName() + " passt nicht mit " +
                "angegebener Einheit " + unit.getUnitType().getName() + " zusammen.");
        }

        Integer priceInBaseRate = totalPrice;

        Deal newDeal = new Deal();
        newDeal.setAmount(productAmount);
        newDeal.setDate(timeService.getCurrentDate());
        newDeal.setLocationName(trader.getLocation().getName());
        newDeal.setPlayer(player);
        newDeal.setPrice(priceInBaseRate);   // Integer
        newDeal.setDiscount(discount);
        newDeal.setProduct(product);
        newDeal.setProductName(product.getName());
        newDeal.setPurchase(true);
        newDeal.setquality(productQuality);
        newDeal.setTrader(trader);
        newDeal.setUnit(unit);
        dealService.validate(newDeal);

        Deal result = dealService.add(newDeal);

        offer.addAmount(-offerAmountDifference);
        if (offer.isEmpty() || removeRemainingOfferAmount) {
            offerDao.remove(offer);
        } else {
            offerDao.update(offer);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public Integer suggesstDiscount(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount) {

        long lookDaysBackwards = 365L;    // consider all deals between player and offer in the last year
        List<Deal> deals = dealService.getAllBetweenPlayerAndTraderLastXDays(player, trader, lookDaysBackwards);

        Integer result = deals.size();  // TODO more sophisticated decides about the discount value

        log.trace("returning " + result);
        return result;
    }

	@Override
	public int getRandomValue(int median, int variation) {
		double rand = Math.random();
		rand *= rand;
		rand *= variation;
		double rand2 = Math.random();
		int result;
		if (rand2 < 0.5) {
			result = (int) (median + rand);
		} else {
			result = (int) (median - rand);
		}
		return result;
	}

	@Override
	public String getRandomName(String culture, boolean male) {
		log.debug("getRandomName called");
		int lastNameMode = -1;
		boolean prefix = false;
		boolean suffix = false;
		boolean maleOnly = false;
		boolean femaleOnly = false;
		final int NORMAL = 0;
		final int BOTH = 1;
		final int FATHER = 2;
		final int MOTHER = 3;
		String sonOf;
		String daughterOf;
		List<String> maleFirstNames = null, femaleFirstNames = null, lastNames = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(nameFile));
			String line = reader.readLine();
			while (line != null) {
				if (line.startsWith("culture "+culture)) {
					line = reader.readLine();
					boolean maleFirstNamesLoaded = false, femaleFirstNamesLoaded = false, lastNamesLoaded = false;
					while (!maleFirstNamesLoaded || !femaleFirstNamesLoaded || !lastNamesLoaded) {
						if (line.startsWith("maleFirstNames")) {
							line = line.substring(line.indexOf("START") + 6);
							maleFirstNames = new ArrayList<>();
							String name;
							while (line.length() > 0) {
								if (line.startsWith("END")) {
									break;
								}
								name = line.substring(0, line.indexOf(", "));
								line = line.substring(line.indexOf(", ") + 2);
								maleFirstNames.add(name);
							}
							maleFirstNamesLoaded = true;
						} else {
							throw new DSARuntimeException("Fehler beim Lesen der Namens Datei (männliche Vornamen fehlen: "+culture+")");
						}
						line = reader.readLine();
						if (line.startsWith("femaleFirstNames")) {
							line = line.substring(line.indexOf("START") + 6);
							femaleFirstNames = new ArrayList<>();
							String name;
							while (line.length() > 0) {
								if (line.startsWith("END")) {
									break;
								}
								name = line.substring(0, line.indexOf(", "));
								line = line.substring(line.indexOf(", ") + 2);
								femaleFirstNames.add(name);
							}
							femaleFirstNamesLoaded = true;
						} else{
							throw new DSARuntimeException("Fehler beim Lesen der Namens Datei (weibliche Vornamen fehlen: "+culture+")");
						}
						line = reader.readLine();
						if (line.startsWith("lastNames")) {

							line = line.substring(line.indexOf("MODE") + 5);
							if (line.startsWith("CHILDOF")) {
								line = line.substring(8);
								if (line.startsWith("BOTH")) {
									lastNameMode = BOTH;
									line = line.substring(5);
								} else if (line.startsWith("FATHER")) {
									lastNameMode = FATHER;
									line = line.substring(5);
								} else if (line.startsWith("MOTHER")) {
									lastNameMode = MOTHER;
									line = line.substring(5);
								} else {
									throw new DSARuntimeException("Fehler beim Lesen der Namens Datei ('MODE CHILDOF <UNKNOWN>')!");
								}
							} else if (line.startsWith("NORMAL")) {
								lastNameMode = NORMAL;
								line = line.substring(7);
							} else {
								throw new DSARuntimeException("Fehler beim Lesen der Namens Datei ('MODE <UNKNOWN>')!");
							}

							if (lastNameMode == NORMAL) {
								line = line.substring(line.indexOf("START") + 6);
								lastNames = new ArrayList<>();
								String name;
								while (line.length() > 0) {
									if (line.startsWith("END")) {
										break;
									}
									name = line.substring(0, line.indexOf(", "));
									line = line.substring(line.indexOf(", ") + 2);
									lastNames.add(name);
								}
							} else {
								if (line.startsWith("PREFIX")) {
									prefix = true;
									line = line.substring(7);
								} else if (line.startsWith("SUFFIX")) {
									suffix = true;
									line = line.substring(7);
								} else {
									throw new DSARuntimeException("Fehler beim Lesen der Namens Datei ('MODE CHILDOF <option> <UNKNOWN>')!");
								}
								line = line.substring(line.indexOf("START") + 6);
								if (line.startsWith("ONLY")) {
									maleOnly = true;
									line = line.substring(5);
								}
								sonOf = line.substring(0, line.indexOf(", "));
								line = line.substring(line.indexOf(", ") + 2);
								if (line.startsWith("ONLY")) {
									femaleOnly = true;
									line = line.substring(5);
								}
								daughterOf = line.substring(0, line.indexOf(", "));
								if (!line.substring(line.indexOf(", ") + 2).startsWith("END")) {
									throw new DSARuntimeException("Fehler beim Lesen der Namens Datei ('MODE CHILDOF <option> <pre/suffix> START <Sohn des> <Tochter der> <UNKNOWN>')!");
								}
								if (prefix) {
									if (male) {
										lastNames = new ArrayList<>();
										if (maleOnly) {
											lastNames.add(sonOf);
										} else {
											for (String n : maleFirstNames) {
												lastNames.add(sonOf + n);
											}
										}
									} else {
										lastNames = new ArrayList<>();
										if (femaleOnly) {
											lastNames.add(daughterOf);
										} else {
											for (String n : femaleFirstNames) {
												lastNames.add(daughterOf + n);
											}
										}
									}
								} else if (suffix) {
									if (male) {
										lastNames = new ArrayList<>();
										if (maleOnly) {
											lastNames.add(sonOf);
										} else {
											for (String n : maleFirstNames) {
												lastNames.add(n + sonOf);
											}
										}
									} else {
										lastNames = new ArrayList<>();
										if (femaleOnly) {
											lastNames.add(daughterOf);
										} else {
											for (String n : femaleFirstNames) {
												lastNames.add(n + daughterOf);
											}
										}
									}
								}
							}
							lastNamesLoaded = true;
						} else{
							throw new DSARuntimeException("Fehler beim Lesen der Namens Datei (Nachnamen fehlen: "+culture+")");
						}
						line = reader.readLine();
						if (maleFirstNames == null || femaleFirstNames == null || lastNames == null) {
							throw new DSARuntimeException("Fehler beim Lesen der Namens Datei (unvollständige Kultur: " + culture + ")");
						}
					}
				} else if (line.startsWith("/////")) {
					break;
				} else {
					line = reader.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			throw new DSARuntimeException("Namens Datei nicht gefunden!");
		} catch (IOException e) {
			throw new DSARuntimeException("Fehler beim Lesen der Namens Datei!");
		}

		String fullName = "";
		if (male) {
			int firstSelection = (int) (Math.random() * maleFirstNames.size());
			fullName += maleFirstNames.get(firstSelection);
		} else {
			int firstSelection = (int) (Math.random() * femaleFirstNames.size());
			fullName += femaleFirstNames.get(firstSelection);
		}
		if (lastNames.size() != 0) {
			fullName += " ";
			int secondSelection = (int) (Math.random() * lastNames.size());
			fullName += lastNames.get(secondSelection);
		}
		return fullName;
	}

	@Override
	public List<String> getAllCultures() {
		List<String> cultures = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(nameFile));
			String line = reader.readLine();
			while (line != null) {
				if (line.startsWith("culture ")) {
					cultures.add(line.substring(8));
				} else if (line.startsWith("/////")) {
					break;
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new DSARuntimeException("Namens Datei nicht gefunden!");
		} catch (IOException e) {
			throw new DSARuntimeException("Fehler beim Lesen der Namens Datei!");
		}
		return cultures;
	}

	@Override
    public Deal buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer productAmount, Integer totalPrice) {
        log.debug("calling buyFromPlayer(" + trader + ", " + player + ", " + product + ", " + productQuality + ", " + unit + ", " + productAmount + ", " + totalPrice + ")");
        Offer offer = null;
        Set<Offer> traderOffers = trader.getOffers();
        for (Offer o : traderOffers) {
            if (o.getProduct().equals(product) && o.getQuality().equals(productQuality)) {
                offer = o;
                break;
            }
        }

        Double offerAmountDifference = unit.exchange(productAmount.doubleValue(), product.getUnit());

        if (totalPrice.doubleValue() < 0) {
            throw new DSAValidationException("Der Preis darf nicht negativ sein");
        }

        if (!unit.getUnitType().equals(product.getUnit().getUnitType())) {
            throw new DSAValidationException("Einheit der Ware " + product.getUnit().getUnitType().getName() + " passt nicht mit " +
                    "angegebener Einheit " + unit.getUnitType().getName() + " zusammen.");
        }

        Integer priceInBaseRate = totalPrice;

        Deal newDeal = new Deal();
        newDeal.setAmount(productAmount);
        newDeal.setDate(timeService.getCurrentDate());
        newDeal.setLocationName(trader.getLocation().getName());
        newDeal.setPlayer(player);
        newDeal.setPrice(priceInBaseRate);   // Integer
        newDeal.setProduct(product);
        newDeal.setProductName(product.getName());
        newDeal.setPurchase(false);
        newDeal.setquality(productQuality);
        newDeal.setTrader(trader);
        newDeal.setUnit(unit);
        dealService.validate(newDeal);

        Deal result = dealService.add(newDeal);

        if (offer == null) {
            offer = new Offer();
            offer.setTrader(trader);
            offer.setAmount(offerAmountDifference);
            offer.setPricePerUnit(priceInBaseRate / productAmount);
            offer.setProduct(product);
            offer.setQuality(productQuality);
            log.info("add " + offer);
            offerDao.add(offer);
        } else {
            offer.addAmount(offerAmountDifference);
            offerDao.update(offer);
        }

        log.trace("returning " + result);
        return result;
    }

    /**
     * @param trader
     * @return a new calculated list of offers this offer at this position has.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Offer> calculateOffers(Trader trader, int number) {
        log.debug("calling calculateOffers(" + trader + ", " + number + ")");

        List<Product> weightProducts = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        float topWeight = 0;
        // calculate weight for each product
        for(AssortmentNature assortmentNature : trader.getCategory().getAssortments().values()) {
            int defaultOccurence = assortmentNature.getDefaultOccurence();
            ProductCategory productCategory = assortmentNature.getProductCategory();
            // get all products from this and the sub Categories
            Set<Product> products = productService.getAllFromProductcategory(productCategory);
            for (Product product : products) {
                if (weightProducts.contains(product)) {
                    break;
                }
                // calculate weight for a product
                float weight = 1f;
                List<RegionBorder> borders;
                try {
                    borders = getCheapestWayBordersBetween(product.getRegions(), trader.getLocation().getRegion());
                } catch (NoPathException e) {
                    break;
                }
                // bordercosts
                for (RegionBorder border : borders) {
                    float x = border.getBorderCost()
                            / (800f - product.getAttribute().getProductTranporabilitySubtrahend());
                    weight -= x;
                }
                weight *= (defaultOccurence / 100f);
                weight *= (product.getOccurence() / 100f);

                topWeight += weight;

                weightProducts.add(product);
                weights.add(topWeight);
            }
        }

        // random pick a weight product, till the offer is full
        Map<Product, Integer> productAmmountMap = new HashMap<>();
        for (int i = 0; i < number; i++) {
            double random = Math.random() * topWeight;
            int j = 0;
            for (float weight : weights) {
                // random picked found
                if (random < weight) {
                    if (productAmmountMap.containsKey(weightProducts.get(j))) {
                        int amount = productAmmountMap.get(weightProducts.get(j));
                        amount++;
                        productAmmountMap.put(weightProducts.get(j), amount);
                    } else {
                        productAmmountMap.put(weightProducts.get(j), 1);
                    }
                    break;
                }
                j++;
            }
        }

        // create Offers
        List<Offer> offers = new ArrayList<Offer>();
        for (Product product : productAmmountMap.keySet()) {
            int amount = productAmmountMap.get(product);

            // random quality distribution
            double amountQualities[] = new double[ProductQuality.values().length];
            if (product.getQuality()) {
                for (int i = 0; i < amount; i++) {
                    int j = 0;
                    double random = Math.random();
                    for (ProductQuality productQuality : ProductQuality.values()) {
                        if (random <= productQuality.getQualityProbabilityValue()) {
                            amountQualities[j]++;
                            break;
                        }
                        j++;
                    }
                }
            } else {
                // if no quality => all to normal
                amountQualities[ProductQuality.NORMAL.getValue()] = amount;
            }
            // create offers
            int i = 0;
            for (ProductQuality productQuality : ProductQuality.values()) {
                if (amountQualities[i] != 0) {
                    Offer offer = new Offer();
                    offer.setTrader(trader);
                    offer.setQuality(productQuality);
                    int price = calculatePricePerUnit(productQuality, product, trader);
                    offer.setPricePerUnit(price);
                    offer.setProduct(product);
                    offer.setAmount(amountQualities[i]);
                    offers.add(offer);
                }
                i++;
            }

        }

	    // add after-coma-part
	    for (Offer offer : offers) {
		    if (offer.getProduct().getUnit().isDevisable()) {
			    double cent = (int) (Math.random()*100);
			    offer.setAmount((double) offer.getAmount().intValue() + cent/100);
		    }
	    }
        List<Offer> result = offers;
        log.trace("returning " + result);
        return result;
    }

    /**
     * @param trader
     * @return a new calculated list of offers this offer at this position has.
     */
    @Override
    public List<Offer> calculateOffers(Trader trader) {
        return calculateOffers(trader, trader.getSize());
    }
    

    /**
     *
     * @param productQuality
     * @param product
     * @param trader
     * @return
     */
    public int calculatePricePerUnit(ProductQuality productQuality, Product product, Trader trader){
        log.debug("calling calculatePricePerUnit(" + productQuality + ", " + product + ", " + trader + ")");
        return (int) (calculatePriceForProduct(product, trader) * productQuality.getQualityPriceFactor());
    }

    /**
     * Returns the price for the product for the given offer
     *
     * @param product
     * @param trader
     * @return the price or -1
     */
    public int calculatePriceForProduct(Product product, Trader trader) {
        log.debug("calling calculatePriceForProduct(" + product + ", " + trader + ")");
        List<RegionBorder> borders;
        int price = product.getCost();

        try {
            borders = getCheapestWayBordersBetween(product.getRegions(), trader.getLocation().getRegion());
        } catch (NoPathException e) {
            throw new DSAValidationException("Preis nicht berechenbar, da keine Verbindung zwischen Produktionsgebieten und Händlergebiet besteht.");
        }

        for (RegionBorder border : borders) {
            price += product.getCost() *
                    (border.getBorderCost() / 100f)
                    * product.getAttribute().getProductTransporabilityFactor();
        }

        log.trace("returning " + price);
        return price;
    }


    @Override
    public void reCalculatePriceForOffer(Trader trader) {
        log.debug("calling reCalculatePriceForOffer(" + trader + ")");
        Set<Offer> offers = trader.getOffers();
        if (offers==null){
            return;
        }
        Iterator i = offers.iterator();
        while(i.hasNext()){
            Offer offer = (Offer)i.next();
            int pricePerUnit = calculatePricePerUnit(offer.getQuality(), offer.getProduct(), trader);
            offer.setPricePerUnit(pricePerUnit);
        }
        trader.setOffers(offers);
    }

    @Override
    public void reCalculatePriceForOfferIfNewPriceIsHigher(Trader trader) {
        log.debug("calling reCalculatePriceForOfferIfNewPriceIsHigher(" + trader + ")");
        Set<Offer> offers = trader.getOffers();
        if (offers==null){
            return;
        }
        Iterator i = offers.iterator();
        while(i.hasNext()){
            Offer offer = (Offer)i.next();
            int pricePerUnit = calculatePricePerUnit(offer.getQuality(), offer.getProduct(), trader);
            if (pricePerUnit > offer.getPricePerUnit()){
                offer.setPricePerUnit(pricePerUnit);
            }
        }
        trader.setOffers(offers);
    }

    private List<RegionBorder> getCheapestWayBordersBetween(Set<Region> productionRegions, Region tradeRegion) throws NoPathException {
        List<RegionBorder> allBorders = regionBorderService.getAll();
        List<Region> allRegions = regionService.getAll();

        List<Region> destinationRegions = (List<Region>) IteratorUtils.toList(productionRegions.iterator());

        return pathService.findShortestPath(allRegions, allBorders, tradeRegion, destinationRegions);
    }


    public void setTraderDao(TraderDao traderDao) {
        log.debug("calling setTraderDao(" + traderDao + ")");
        this.traderDao = traderDao;
    }

	public void setMovingTraderDao(MovingTraderDao movingTraderDao) {
        log.debug("calling setMovingTraderDao(" + movingTraderDao + ")");
        this.movingTraderDao = movingTraderDao;
	}

    public void setProductService(ProductService productService) {
        log.debug("calling setProductService(" + productService + ")");
        this.productService = productService;
    }

    public void setPathService(PathService pathService) {
        log.debug("calling setPathService(" + pathService + ")");
        this.pathService = pathService;
    }

    public void setRegionService(RegionService regionService) {
        log.debug("calling setRegionService(" + regionService + ")");
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService regionBorderService) {
        log.debug("calling setRegionBorderService(" + regionBorderService + ")");
        this.regionBorderService = regionBorderService;
    }

    public void setOfferDao(OfferDao offerDao) {
        log.debug("calling setOfferDao(" + offerDao + ")");
        this.offerDao = offerDao;
    }

    /**
     * Validates a Trader
     *
     * @param trader must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if location is not valid
     */
    private void validate(Trader trader) throws DSAValidationException {
        log.debug("calling validate(" + trader + ")");
        Set<ConstraintViolation<Trader>> violations = validator.validate(trader);
        if (violations.size() > 0) {
            throw new DSAValidationException("Händler ist nicht valide.", violations);
        }
    }

    /**
     * Validates an Offer
     *
     * @param offer must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if location is not valid
     */
    private void validateOffer(Offer offer) throws DSAValidationException {
        log.debug("calling validateOffer(" + offer + ")");
        Set<ConstraintViolation<Offer>> violations = validator.validate(offer);
        if (violations.size() > 0) {
            throw new DSAValidationException("Händler ist nicht valide.", violations);
        }
    }

	/**
	 * Returns a list of Offers of the specified offer.
	 *
	 * @param trader the offer whose offers are requested.
	 * @return a list of the offer's offers.
	 */
	@Override
	public Collection<Offer> getOffers(Trader trader) {
        log.debug("calling getOffers(" + trader + ")");
        // Initialize the set the mëh way
		trader.getOffers().size();

		return trader.getOffers();
	}

    public void setTimeService(TimeService timeService) {
        log.debug("calling setTimeService(" + timeService + ")");
        this.timeService = timeService;
    }

    public void setDealService(DealService dealService) {
        log.debug("calling setDealService(" + dealService + ")");
        this.dealService = dealService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        log.debug("calling setCurrencyService(" + currencyService + ")");
        this.currencyService = currencyService;
    }
}
