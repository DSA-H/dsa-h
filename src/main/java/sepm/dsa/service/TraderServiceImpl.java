package sepm.dsa.service;

import org.apache.commons.collections.IteratorUtils;
import org.hibernate.SessionFactory;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.path.NoPathException;
import sepm.dsa.service.path.PathService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;

@Transactional(readOnly = true)
public class TraderServiceImpl implements TraderService {
    private static final Logger log = LoggerFactory.getLogger(TraderServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TraderDao traderDao;
    private ProductService productService;
    private PathService<RegionBorder> pathService;
    private RegionService regionService;
    private RegionBorderService regionBorderService;

	private SessionFactory sessionFactory;

	@Override
    public Trader get(int id) {
        log.debug("calling get(" + id + ")");
        Trader result = traderDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Trader add(Trader t) {
        log.debug("calling addConnection(" + t + ")");
        validate(t);
        Trader trader = traderDao.add(t);
        HashSet<Offer> offers = new HashSet<>(calculateOffers(t));
        t.setOffers(offers);
        update(t); // @TODO refactor!
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
    public List<Trader> getAllForLocation(Location location) {
        log.debug("calling getAll()");
        List<Trader> result = traderDao.getAllByLocation(location);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Trader> getAllByCategory(TraderCategory traderCategory) {
        log.debug("calling getAll()");
        List<Trader> result = traderDao.getAllByCategory(traderCategory);
        log.trace("returning " + result);
        return result;
    }

    /**
     * @param trader
     * @return a new calculated list of offers this trader at this position has.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Offer> calculateOffers(Trader trader) {
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

                topWeight += weight;

                weightProducts.add(product);
                weights.add(topWeight);
            }
        }

        // random pick a weight product, till the trader is full
        Map<Product, Integer> productAmmountMap = new HashMap<>();
        for (int i = 0; i < trader.getSize(); i++) {
            double random = Math.random() * topWeight;
            int j = 0;
            for (float weight : weights) {
                // random picked found
                if (random < weight) {
                    if (productAmmountMap.containsKey(weightProducts.get(j))) {
                        int amount = productAmmountMap.get(weightProducts.get(j));// TODO @johannes here was: int amount = productAmmountMap.get(productAmmountMap);
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
            int ammount = productAmmountMap.get(product);

            // random quality distribution
            int amountQualities[] = new int[ProductQuality.values().length];
            if (product.getQuality()) {
                for (int i = 0; i < ammount; i++) {
                    int j = 0;
                    for (ProductQuality productQuality : ProductQuality.values()) {
                        double random = Math.random();
                        if (random < productQuality.getQualityProbabilityValue()) {
                            amountQualities[j]++;
                            break;
                        }
                        j++;
                    }
                }
            } else {
                amountQualities[0] = ammount;
            }
            // create offers
            int i = 0;
            for (ProductQuality productQuality : ProductQuality.values()) {
                if (amountQualities[i] == 0) {
                    break;
                }
                Offer offer = new Offer();
                offer.setTrader(trader);
                offer.setQuality(productQuality);
                int price = (int) (calculatePriceForProduct(product, trader) * productQuality.getQualityPriceFactor());
                offer.setPricePerUnit(price);
                offer.setProduct(product);
                offer.setAmount(amountQualities[i]);
                offers.add(offer);
                i++;
            }

        }
        return offers;
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
}
