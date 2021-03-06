package sepm.dsa.service.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.MathUtil;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@Transactional
public class TraderServiceTest extends AbstractDatabaseTest {

    private static final Logger log = LoggerFactory.getLogger(TraderServiceTest.class);

    @Autowired
    private TraderService traderService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TraderCategoryService traderCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private DealService dealService;


    @Test
    public void testAdd() throws Exception {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);
        trader.setSize(20);
        Location l = locationService.get(1);
        trader.setLocation(l);
        trader.setxPos(1);
        trader.setyPos(2);
        TraderCategory tc = traderCategoryService.get(1);
        tc.setName("tc1");
        trader.setCategory(tc);

        traderService.add(trader);

        saveCancelService.save();

        Trader persistedTrader = traderService.get(trader.getId());
        assertTrue(persistedTrader != null);

        Set<Offer> persistentOffers = persistedTrader.getOffers();
        int offersAmount = 0;
        for(Offer offer : persistentOffers) {
            offersAmount += offer.getAmount();
        }
        assertTrue("offersAmount = " + offersAmount + " traderSize = " + trader.getSize() + ", should be equal!", offersAmount == trader.getSize());
    }

    @Test
    public void testRemove() throws Exception {
        Trader trader = traderService.get(2);
        traderService.remove(trader);
        saveCancelService.save();

        assertNull(traderService.get(2));
    }

    @Test
    public void testGet() throws Exception {
        Trader trader = traderService.get(2);
        assertNotNull(trader);
    }

    @Test
    public void testGetAllForLocation() throws Exception {
        Location location = locationService.get(1);

        List<Trader> traders = traderService.getAllForLocation(location);
        Trader t1 = traderService.get(1);
        Trader t2 = traderService.get(2);
        assertTrue(traders.contains(t1));
        assertTrue(traders.contains(t2));
    }

    @Test
    public void testGetAllbyCategory() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(1);

        List<Trader> traders = traderService.getAllByCategory(traderCategory);
        Trader t1 = traderService.get(1);
        assertTrue(traders.contains(t1));
    }

    @Test
    public void calculatePriceForProduct_alwaysPositive() {
        Trader trader = traderService.get(1);
        Collection<AssortmentNature> assortments = trader.getCategory().getAssortments().values();
        for (AssortmentNature a : assortments) {
            for (Product p : a.getProductCategory().getProducts()) {
                assertTrue("Preis muss positiv sein", traderService.calculatePriceForProduct(p, trader, false) > 0);
            }
        }
        assertTrue("There were no assortments set in test data", assortments.size() > 0);
    }

    @Test
    public void calculateOffers_OffersShouldNotExceedTraderSpace() {
        Trader trader = traderService.get(1);
        int traderSize = trader.getSize();

        int offersAmount = 0;
        List<Offer> offers = traderService.calculateOffers(trader);
        for (Offer o : offers) {
            offersAmount += o.getAmount();
        }
        assertTrue("TraderService didn't find a offer to suggest, update test data", offers.size() > 0);
        assertTrue("offersAmount = " + offersAmount + " traderSize = " + traderSize + ", should be equal!", offersAmount == traderSize);
    }

    @Test
    public void calculateOffers_OffersShouldNotExceedTraderSpace3() {
        Trader trader = traderService.get(2);
        int traderSize = trader.getSize();

        int offersAmount = 0;
        List<Offer> offers = traderService.calculateOffers(trader);
        for (Offer o : offers) {
            offersAmount += o.getAmount();
        }
        assertTrue("TraderService didn't find a offer to suggest, update test data", offers.size() > 0);
        assertTrue("offersAmount = " + offersAmount + " traderSize = " + traderSize + ", should be equal!", offersAmount == traderSize);
    }

    @Test
    public void calculateOffers_OffersShouldNotExceedTraderSpace2() {
        Trader trader = traderService.get(2);

        List<Offer> offers = traderService.calculateOffers(trader);
        for (Offer o : offers) {
            Product p = o.getProduct();
            boolean contains = false;
            for (AssortmentNature a : trader.getCategory().getAssortments().values()) {
                if (a.getProductCategory().getProducts().contains(p)) {
                    contains = true;
                    break;
                }
            }
            assertTrue("Product in Trader Offer ist not in connected to the trader categories normal product categories", contains);
        }
        assertTrue("TraderService didn't find a offer to suggest, update test data", offers.size() > 0);
    }

    @Test
    public void calculateOffers_OffersShouldbeEmpty() {
        Trader trader = traderService.get(3);

        List<Offer> offers = traderService.calculateOffers(trader);

        assertTrue("TraderService find a offer to suggest, update test data", offers.isEmpty());
    }

    /**
     * Trader 4 has a own TraderCategory with 2 ProductCategories: Categorie1 (normal), Categorie6 which contains only
     * product8 which is producted only in region7 which has no borders. That means although prodcut8 is in the
     * tradercategory "template" there is no way to the product and it should not appear in the offer. The offer should
     * be filled with products from Categorie1.
     */
    @Test
    public void calculateOffers_NotReachableRegion() {
        Trader trader = traderService.get(4);
        int traderSize = trader.getSize();

        List<Offer> offers = traderService.calculateOffers(trader);

        int offersAmount = 0;
        for (Offer o : offers) {
            offersAmount += o.getAmount();
            Product p = o.getProduct();
            assertFalse(p.getId() == 8);
        }
        assertTrue("TraderService didn't find a offer to suggest, update test data", offers.size() > 0);
        assertTrue(offersAmount == traderSize);
    }

    @Test
    public void sellToPlayer_shouldCreateDealAndReduceOfferAmountInsertDeal() {

        Trader trader = traderService.get(1);
        Player player = playerService.get(1);
        Product product = productService.get(1);
        ProductQuality productQuality = ProductQuality.MANGELHAFT;
        Unit unit = product.getUnit();
        Integer amount = 5;
        Integer totalPrice = 40000;
        Currency currency = currencyService.get(1);
        Integer discount = 0;

        Offer offerBefore = null;
        for (Offer o : trader.getOffers()) {
            if (o.getProduct().equals(product) && o.getQuality().equals(productQuality)) {
                offerBefore = o;
                break;
            }
        }
        double offerAmountBefore = offerBefore.getAmount();

        int dealSizeBefore = dealService.getAll().size();
        int offerSizeBefore = trader.getOffers().size();

        Deal deal = traderService.sellToPlayer(trader, player, product, productQuality, unit, amount, totalPrice, discount, false);
        saveCancelService.save();

        Trader traderAfter = traderService.get(1);


        Offer offerAfter = null;
        for (Offer o : traderAfter.getOffers()) {
            if (o.getProduct().equals(product) && o.getQuality().equals(productQuality)) {
                offerAfter = o;
                break;
            }
        }

        assertEquals(amount, deal.getAmount());
//        assertEquals(currencyService.exchangeToBaseRate(currency, totalPrice), deal.getPrice());
        assertEquals(totalPrice, deal.getPrice());
        assertEquals(unit, deal.getUnit());
        assertEquals(true, deal.isPurchase());
        assertEquals(dealSizeBefore + 1, dealService.getAll().size());
        assertEquals(offerSizeBefore, traderAfter.getOffers().size());
        assertTrue("Amount afterwards was expected to be " + (offerAmountBefore - amount) + ", but was " + offerAfter.getAmount(),
                Math.abs((offerAmountBefore - amount) - offerAfter.getAmount()) < MathUtil.EPSILON);

        dealService.remove(deal);
        saveCancelService.save();

    }


    @Test
    public void buyFromPlayer_shouldCreateDealAndReduceOfferAmountInsertDeal() {

        Trader trader = traderService.get(1);
        Player player = playerService.get(1);
        Product product = productService.get(1);
        ProductQuality productQuality = ProductQuality.NORMAL;
        Unit unit = product.getUnit();
        Integer amount = 5;
        Integer totalPrice = 40000;
//        Currency currency = currencyService.get(3);

        int dealSizeBefore = dealService.getAll().size();
        int offerSizeBefore = trader.getOffers().size();

        Deal deal = traderService.buyFromPlayer(trader, player, product, productQuality, unit, amount, totalPrice);
        saveCancelService.save();

        Trader traderAfter = traderService.get(1);


        Offer offerAfter = null;
        for (Offer o : traderAfter.getOffers()) {
            if (o.getProduct().equals(product) && o.getQuality().equals(productQuality)) {
                offerAfter = o;
                break;
            }
        }

        assertEquals(amount, deal.getAmount());
//        assertEquals(currencyService.exchangeToBaseRate(currency, totalPrice), deal.getPrice());
        assertEquals(totalPrice, deal.getPrice());
        assertEquals(unit, deal.getUnit());
        assertEquals(false, deal.isPurchase());
        assertEquals(dealSizeBefore + 1, dealService.getAll().size());
        assertEquals(offerSizeBefore + 1, traderAfter.getOffers().size());
        assertNotNull(offerAfter);
        assertTrue("Amount afterwards was expected to be " + amount + ", but was " + offerAfter.getAmount(),
                Math.abs(amount - offerAfter.getAmount()) < MathUtil.EPSILON);

        dealService.remove(deal);
        saveCancelService.save();

    }

    @Test
    public void suggestDiscount_zeroIfNoDealsWithTraderBefore() {
        Trader trader = traderService.get(1);
        Player player = playerService.get(1);
        Product product = productService.get(1);
        ProductQuality productQuality = ProductQuality.NORMAL;
        Unit unit = product.getUnit();
        Integer amount = 5;

        int discount = traderService.suggesstDiscount(trader, player, product, productQuality, unit, amount);

        assertEquals("The discount must be zero if there were no deals between this trader and player before", 0, discount);
    }



}