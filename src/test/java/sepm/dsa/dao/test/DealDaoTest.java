package sepm.dsa.dao.test;

import static org.junit.Assert.assertEquals;
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

public class DealDaoTest extends AbstractDatabaseTest {

    @Autowired
    private DealDao dealDao;
    private ProductService productService;
    private TraderService traderService;
    private PlayerService playerService;
    private UnitService unitService;



    @Ignore
    @Test
    public void test() {

    }
    // TODO test things...

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

//    public void playerDealsWithTraderInTimeRange


}
