package sepm.dsa.dao.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.model.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by Jotschi on 18.05.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class TraderDaoTest {

    @Autowired
    private TraderDao traderDao;

    @Test
    @DatabaseSetup("/testData.xml")
    public void add_shouldPersistEntity() {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);
        Location l = new Location();
        l.setName("l1");
        l.setHeight(120);
        Region r = new Region();
        r.setName("r1");
        r.setTemperature(Temperature.MEDIUM);
        r.setRainfallChance(RainfallChance.DESSERT);
        l.setRegion(r);
        l.setxCoord(1);
        l.setyCoord(7);
        l.setSize(TownSize.MEDIUM);
        trader.setLocation(l);
        TraderCategory tc = new TraderCategory();
        tc.setName("tc1");
        trader.setCategory(tc);

        traderDao.add(trader);

        Trader persistedTrader = traderDao.get(trader.getId());
        assertTrue(persistedTrader != null);

        traderDao.remove(trader);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void add_incompleteShouldNotPersist() {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);

        traderDao.add(trader);

        Trader persistedTrader = traderDao.get(trader.getId());
        assertTrue(persistedTrader == null);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void update_ShouldPersistEntity() {
        Trader persistedTrader = traderDao.get(2);

        persistedTrader.setComment("xyz123");

        traderDao.update(persistedTrader);

        persistedTrader = traderDao.get(2);

        assertTrue(persistedTrader != null);
        assertTrue(persistedTrader.getName().equals("Megatron"));
        assertTrue(persistedTrader.getComment().equals("xyz123"));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void update_TraderWithOffersShouldPersistEntity() {
        Trader persistedTrader = traderDao.get(1);

        Offer o1 = new Offer();
        o1.setAmount(10);
        o1.setPricePerUnit(170);
        Product p1 = new Product();
        p1.setName("testproduct");
        p1.setAttribute(ProductAttribute.NORMAL);
        p1.setCost(140);
        p1.setQuality(true);
        o1.setProduct(p1);
        o1.setQuality(ProductQuality.SCHLECHT);
        persistedTrader.getOffers().add(o1);

        traderDao.update(persistedTrader);

        persistedTrader = traderDao.get(1);
        assertTrue(persistedTrader != null);
        assertTrue(persistedTrader.getOffers().contains(o1));
    }

}
