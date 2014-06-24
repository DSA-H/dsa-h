package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.DealDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DealServiceTest extends AbstractDatabaseTest {

    private DealService dealService;

    @Autowired
    private DealDao dealDao;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UnitService unitService;

    //mocked
    private TimeService timeServiceMock;

    public DealServiceTest() {
    }

    @Before
    public void setUp() {
        timeServiceMock = mock(TimeService.class);

        when(timeServiceMock.getCurrentDate()).thenReturn(new DSADate(450));

        DealServiceImpl dealServiceImpl = new DealServiceImpl();
        dealServiceImpl.setTimeService(timeServiceMock);
        dealServiceImpl.setDealDao(dealDao);
        dealService = dealServiceImpl;
    }

    @Test
    public void getAllBetweenPlayerAndTraderLastXDays_returnsCorrectEntries() {
        Player player = playerService.get(1);
        Trader trader = traderService.get(2);
        Deal deal1 = dealService.get(2);
        Deal deal2 = dealService.get(3);
        Deal deal3 = dealService.get(4);
        Deal deal4 = dealService.get(5);
        Set<Deal> expectedDeals = new HashSet<>(Arrays.asList(deal1, deal2, deal3, deal4));
        Set<Deal> deals = new HashSet<>(dealService.getAllBetweenPlayerAndTraderLastXDays(player, trader, 365));

        assertEquals(expectedDeals, deals);
    }


    @Test
    public void add_shouldPersistEntry() {

        Product product = productService.get(1);
        Integer discount = 0;
        Integer amount = 10;
        Trader trader = traderService.get(1);
        long date = 0;
        String locationName = trader.getLocation().getName();
        Player player = playerService.get(1);
        Integer price = 200;
        String productName = product.getName();
        boolean purchase = true;
        ProductQuality quality = ProductQuality.NORMAL;
        Unit unit = unitService.get(1);

        Deal deal = new Deal();
        deal.setProduct(product);
        deal.setDiscount(discount);
        deal.setAmount(amount);
        deal.setTrader(trader);
        deal.setDate(date);
        deal.setLocationName(locationName);
        deal.setPlayer(player);
        deal.setPrice(price);
        deal.setProductName(productName);
        deal.setPurchase(purchase);
        deal.setquality(quality);
        deal.setUnit(unit);

        int dealsBefore = dealService.getAll().size();

        dealService.add(deal);
        saveCancelService.save();

        assertEquals(dealsBefore + 1, dealService.getAll().size());

    }

    @Test
    public void update_shouldUpdateEntity() {
        Deal deal = dealService.get(1);
        deal.setDiscount(10);

        dealService.update(deal);
        saveCancelService.save();

        assertEquals(new Integer(10), dealService.get(1).getDiscount());
    }

    @Test
    public void remove_shouldRemoveEntity() {
        int dealsBefore = dealService.getAll().size();
        Deal deal = dealService.get(1);

        dealService.remove(deal);
        saveCancelService.save();

        assertEquals(dealsBefore - 1, dealService.getAll().size());
    }


    @Test
    public void get_shouldRetrieveEntity() {
        assertNotNull(dealService.get(1));
    }

    @Test
    public void get_shouldReturnNull() {
        assertNull(dealService.get(100));
    }

    @Test
    public void getAll_shouldRetrieveAll() {
        assertEquals(9, dealService.getAll().size());
    }

    @Test
    public void getAllByProduct_shouldRetrieveAllFilteredByProduct() {
        Product product = productService.get(1);
        Deal deal1 = dealService.get(1);
        Deal deal2 = dealService.get(2);
        Deal deal3 = dealService.get(3);
        Set<Deal> expected = new HashSet<>(Arrays.<Deal>asList(deal1, deal2, deal3));

        Set<Deal> deals = new HashSet<>(dealService.getAllByProduct(product));

        assertEquals(expected, deals);
    }


}
