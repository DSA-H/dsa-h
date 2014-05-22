package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.*;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderCategoryService;
import sepm.dsa.service.TraderService;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class TraderServiceTest extends TestCase {

    @Autowired
    private TraderService traderService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TraderCategoryService traderCategoryService;


    @Test
    @DatabaseSetup("/testData.xml")
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

        Trader persistedTrader = traderService.get(trader.getId());
        assertTrue(persistedTrader != null);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        Trader trader = traderService.get(2);
        traderService.remove(trader);

        assertNull(traderService.get(2));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() throws Exception {
        Trader trader = traderService.get(2);
        assertNotNull(trader);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAllForLocation() throws Exception {
        Location location = locationService.get(1);

        List<Trader> traders = traderService.getAllForLocation(location);
        Trader t1 = traderService.get(1);
        Trader t2 = traderService.get(2);
        assertTrue(traders.contains(t1));
        assertTrue(traders.contains(t2));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAllbyCategory() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(1);

        List<Trader> traders = traderService.getAllByCategory(traderCategory);
        Trader t1 = traderService.get(1);
        assertTrue(traders.contains(t1));
    }
}