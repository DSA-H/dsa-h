package sepm.dsa.service;

import org.apache.commons.collections.IteratorUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;

public class TraderServiceImpl implements TraderService {
    private static final Logger log = LoggerFactory.getLogger(TraderServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TraderDao traderDao;
    private ProductService productService;
    private PathService<RegionBorder> pathService;
    private RegionService regionService;
    private RegionBorderService regionBorderService;
    private TimeService timeService;
    private OfferDao offerDao;

	private SessionFactory sessionFactory;

	@Override
    public Trader get(int id) {
        log.debug("calling get(" + id + ")");
        Trader result = traderDao.get(id);
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
        log.debug("calling addConnection(" + t + ")");
        validate(t);
        Trader trader = traderDao.add(t);
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
        return traderDao.update(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Trader t) {
        log.debug("calling removeConnection(" + t + ")");
        traderDao.remove(t);
    }

    @Override
    public List<Trader> getAll() {
        log.debug("calling getAll()");
        List<Trader> result = traderDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Trader recalculateOffers(Trader t) {
        log.debug("calling addConnection(" + t + ")");
        Set<Offer> oldOffers = t.getOffers();
        Iterator i = oldOffers.iterator();
        while(i.hasNext()){
            Offer o = (Offer)i.next();
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
        List<Trader> traders = traderDao.getAll();
        List<MovingTrader> result = new ArrayList<>();
        for(Trader trader : traders) {
            if(trader instanceof MovingTrader) {
                result.add((MovingTrader)trader);
            }
        }
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Trader> getAllForLocation(Location location) {
        log.debug("calling getAllForLocation()");
        List<Trader> result = traderDao.getAllByLocation(location);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Trader> getAllByCategory(TraderCategory traderCategory) {
        log.debug("calling getAllByCategory()");
        List<Trader> result = traderDao.getAllByCategory(traderCategory);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public void sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, BigDecimal totalPrice, Currency currency) {
        // TODO discuss Currency question with jotschi

        Deal newDeal = new Deal();
        newDeal.setAmount(amount);
        newDeal.setDate(timeService.getCurrentDate());
        newDeal.setLocationName(trader.getLocation().getName());
        newDeal.setPlayer(player);
        newDeal.setPrice(totalPrice);
        newDeal.setProduct(product);
        newDeal.setProductName(product.getName());
        newDeal.setPurchase(false);
        newDeal.setquality(productQuality);
        newDeal.setTrader(trader);
        newDeal.setUnit(unit);

    }

    @Override
    public void buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, BigDecimal totalPrice, Currency currency) {
        // TODO discuss Currency question with jotschi

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
            int amountQualities[] = new int[ProductQuality.values().length];
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
}
