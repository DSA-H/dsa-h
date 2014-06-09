package sepm.dsa.service;

import org.apache.commons.collections.IteratorUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.dao.MovingTraderDao;
import sepm.dsa.dao.OfferDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.model.Currency;
import sepm.dsa.service.path.NoPathException;
import sepm.dsa.service.path.PathService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

	private SessionFactory sessionFactory;

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
		    trader = movingTraderDao.add((MovingTrader) t);
	    } else {
		    trader = traderDao.add(t);
	    }
		List<Offer> offers = calculateOffers(t);
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
		    return movingTraderDao.update((MovingTrader) t);
	    }
        return traderDao.update(t);
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

        //TODO: Remove is missing (error: ConcurrentModifitcationException)
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
    public Deal sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, List<CurrencyAmount> totalPrice, Integer discount) {
        Integer baseValuePrice = currencyService.exchangeToBaseRate(totalPrice);
        return sellToPlayer(trader, player, product, productQuality, unit, amount, baseValuePrice, discount);
    }

    @Override
    public Deal buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, List<CurrencyAmount> totalPrice) {
        Integer baseValuePrice = currencyService.exchangeToBaseRate(totalPrice);
        return buyFromPlayer(trader, player, product, productQuality, unit, amount, baseValuePrice);
    }

    //     * @throws sepm.dsa.exceptions.DSAValidationException if trader does not have the product with this quality <br />
//     *      or the amount is greater than the trader offers <br />
//     *      or unit does does not match the product unit <br />
//     *      or totalPrice is negative
    @Override
    public Deal sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer productAmount, Integer totalPrice, Integer discount) {
        // TODO discuss Currency question with jotschi

        Offer offer = null;
        Set<Offer> traderOffers = trader.getOffers(); // get from dao
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
        log.info("remaing amount would be " + remainingAmount);
        if (remainingAmount < 0) {
            throw new DSAValidationException("Der Händler hat nicht genug Waren dieser Art in dieser Qualitätsstufe");
        }

        if (totalPrice.doubleValue() < 0) {
            throw new DSAValidationException("Der Preis darf nicht negativ sein");
        }

        if (!unit.getUnitType().equals(offer.getProduct().getUnit().getUnitType())) {     // offer needs unit?
            throw new DSAValidationException("Einheit der Ware " + product.getUnit().getUnitType().getName() + " passt nicht mit " +
                "angegebener Einheit " + unit.getUnitType().getName() + " zusammen.");
        }

//        Integer priceInBaseRate = currencyService.exchangeToBaseRate(currency, totalPrice);
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
        validateDeal(newDeal);

        Deal result = dealService.add(newDeal);

        offer.addAmount(-offerAmountDifference);
        if (offer.isEmpty()) {
            offerDao.remove(offer);
        } else {
            offerDao.update(offer);
        }

        return result;
    }

    @Override
    public Integer suggesstDiscount(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount) {

        long lookDaysBackwards = 365L;    // consider all deals between player and trader in the last year
        List<Deal> deals = dealService.getAllBetweenPlayerAndTraderLastXDays(player, trader, lookDaysBackwards);

        // TODO some logic that decides about the discount value
        return deals.size();
    }

    @Override
    public Deal buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer productAmount, Integer totalPrice) {

        // TODO discuss Currency question with jotschi

        Offer offer = null;
        Set<Offer> traderOffers = trader.getOffers(); // get from dao
        for (Offer o : traderOffers) {
            if (o.getProduct().equals(product) && o.getQuality().equals(productQuality)) {
                offer = o;
                break;
            }
        }

//        TODO Jotschi: Der Haendler hat bekommt ja normalerweise immer ein volles Sortiment wenn Zeit vorwaerts gestellt wird
//        TODO          wenn jetzt ein Spieler etwas verkaufen will, muss er zuerst was vom Haendler kaufen, sonst hat dieser keinen Platz. Bitte klaeren mit Michael
        Double offerAmountDifference = unit.exchange(productAmount.doubleValue(), product.getUnit());
//        double newUsedspace = trader.usedSpace() + offerAmountDifference;
//        if (newUsedspace > trader.getSize()) {
//            throw new DSAValidationException("Der Händler kann so viele Waren nicht besitzen");
//        }

        if (totalPrice.doubleValue() < 0) {
            throw new DSAValidationException("Der Preis darf nicht negativ sein");
        }

        if (!unit.getUnitType().equals(product.getUnit().getUnitType())) {     // offer needs unit?
            throw new DSAValidationException("Einheit der Ware " + product.getUnit().getUnitType().getName() + " passt nicht mit " +
                    "angegebener Einheit " + unit.getUnitType().getName() + " zusammen.");
        }

//        Integer priceInBaseRate = currencyService.exchangeToBaseRate(currency, totalPrice);
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
        validateDeal(newDeal);

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

        return result;

    }

    /**
     * todo: offers have to integrate product-occurerence (in a later ms)
     * @param trader
     * @return a new calculated list of offers this trader at this position has.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Offer> calculateOffers(Trader trader, int number) {
        log.debug("calling calculateOffers()");

        // TODO Jotschi: Offer.amount changed from Integer to Double, might cause problems!

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

        // random pick a weight product, till the trader is full
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
        List<Offer> offers = new ArrayList<>();
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
        return offers;
    }

    /**
     * @param trader
     * @return a new calculated list of offers this trader at this position has.
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
        return (int) (calculatePriceForProduct(product, trader) * productQuality.getQualityPriceFactor());
    }

    /**
     * Returns the price for the product for the given trader
     *
     * @param product
     * @param trader
     * @return the price or -1
     */
    public int calculatePriceForProduct(Product product, Trader trader) {
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

        return price;
    }


    @Override
    public void reCalculatePriceForOffer(/*Set<Offer> offers, */Trader trader) {
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
    public void reCalculatePriceForOfferIfNewPriceIsHigher(/*Set<Offer> offers, */Trader trader) {
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
        this.traderDao = traderDao;
    }

	public void setMovingTraderDao(MovingTraderDao movingTraderDao) {
		this.movingTraderDao = movingTraderDao;
	}

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setPathService(PathService pathService) {
        this.pathService = pathService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService regionBorderService) {
        this.regionBorderService = regionBorderService;
    }

    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    /**
     * Validates a Trader
     *
     * @param trader must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if location is not valid
     */
    private void validate(Trader trader) throws DSAValidationException {
        Set<ConstraintViolation<Trader>> violations = validator.validate(trader);
        if (violations.size() > 0) {
            throw new DSAValidationException("Händler ist nicht valide.", violations);
        }
    }

    /**
     * Validates a Deal
     *
     * @param deal must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if location is not valid
     */
    private void validateDeal(Deal deal) throws DSAValidationException {
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        if (violations.size() > 0) {
            throw new DSAValidationException("Deal ist nicht valide.", violations);
        }
    }

	/**
	 * Returns a list of Offers of the specified trader.
	 *
	 * @param trader the trader whose offers are requested.
	 * @return a list of the trader's offers.
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Offer> getOffers(Trader trader) {
		// Initialize the set the mëh way
		trader.getOffers().size();

		return trader.getOffers();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
}
