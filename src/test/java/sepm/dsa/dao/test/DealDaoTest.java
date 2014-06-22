package sepm.dsa.dao.test;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.DealDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;
import sepm.dsa.service.PlayerService;
import sepm.dsa.service.ProductService;
import sepm.dsa.service.TraderService;
import sepm.dsa.service.UnitService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DealDaoTest extends AbstractDatabaseTest {

    @Autowired
    private DealDao dealDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private UnitService unitService;


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

        int dealsBefore = dealDao.getAll().size();

        dealDao.add(deal);
        saveCancelService.save();

        assertEquals(dealsBefore + 1, dealDao.getAll().size());

    }

    @Test
    public void update_shouldUpdateEntity() {
        Deal deal = dealDao.get(1);
        deal.setDiscount(10);

        dealDao.update(deal);
        saveCancelService.save();

        assertEquals(new Integer(10), dealDao.get(1).getDiscount());
    }

    @Test
    public void remove_shouldRemoveEntity() {
        int dealsBefore = dealDao.getAll().size();
        Deal deal = dealDao.get(1);

        dealDao.remove(deal);
        saveCancelService.save();

        assertEquals(dealsBefore - 1, dealDao.getAll().size());
    }


    @Test
    public void get_shouldRetrieveEntity() {
        assertNotNull(dealDao.get(1));
    }

    @Test
    public void get_shouldReturnNull() {
        assertNull(dealDao.get(100));
    }

    @Test
    public void getAll_shouldRetrieveAll() {
        assertEquals(9, dealDao.getAll().size());
    }

    @Test
    public void getAllByProduct_shouldRetrieveAllFilteredByProduct() {
        Product product = productService.get(1);
        Deal deal1 = dealDao.get(1);
        Deal deal2 = dealDao.get(2);
        Deal deal3 = dealDao.get(3);
        Set<Deal> expected = new HashSet<>(Arrays.<Deal>asList(deal1, deal2, deal3));

        Set<Deal> deals = new HashSet<>(dealDao.getAllByProduct(product));

        assertEquals(expected, deals);
    }

    @Test
    public void playerDealsWithTraderInTimeRange_should() {

        long fromDate = 0;
        long toDate = 365;

        Player player = playerService.get(1);
        Trader trader = traderService.get(2);
        Deal deal1 = dealDao.get(1);
        Deal deal2 = dealDao.get(2);
        Deal deal3 = dealDao.get(3);
        Deal deal4 = dealDao.get(4);
        Set<Deal> expectedDeals = new HashSet<>(Arrays.asList(deal1, deal2, deal3, deal4));
        Set<Deal> deals = new HashSet<>(dealDao.playerDealsWithTraderInTimeRange(player, trader, fromDate, toDate));

        assertEquals(expectedDeals, deals);
    }


}
