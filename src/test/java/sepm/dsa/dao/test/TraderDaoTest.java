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
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    @Autowired
    private LocationDao locationDao;
    @Autowired
    private TraderCategoryDao traderCategoryDao;
    @Autowired
    private ProductDao productDao;

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
        trader.setSize(20);
        Location l = locationDao.get(1);
        trader.setLocation(l);
        trader.setxPos(1);
        trader.setyPos(2);
        TraderCategory tc = traderCategoryDao.get(1);
        tc.setName("tc1");
        trader.setCategory(tc);

        traderDao.add(trader);

        Trader persistedTrader = traderDao.get(trader.getId());
        assertNotNull(persistedTrader);
    }

    @Test(expected = DSARuntimeException.class)
    @DatabaseSetup("/testData.xml")
    public void delete_shouldPersistEntity() {
        Trader trader = traderDao.get(1);

        traderDao.remove(trader);

        Trader persistedTrader = traderDao.get(1);
    }

    @Test(expected = org.hibernate.PropertyValueException.class)
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
        assertNull(persistedTrader);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void update_ShouldPersistEntity() {
        Trader persistedTrader = traderDao.get(2);

        persistedTrader.setComment("xyz123");

        traderDao.update(persistedTrader);

        persistedTrader = traderDao.get(2);

        assertNotNull(persistedTrader);
        assertTrue(persistedTrader.getName().equals("Megatron"));
        assertTrue(persistedTrader.getComment().equals("xyz123"));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void add_TraderWithOffersShouldPersistEntity() {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);
        trader.setSize(20);
        Location l = locationDao.get(1);
        trader.setLocation(l);
        trader.setxPos(1);
        trader.setyPos(2);
        TraderCategory tc = traderCategoryDao.get(1);
        tc.setName("tc1");
        trader.setCategory(tc);

        Offer o1 = new Offer();
        o1.setAmount(10);
        o1.setPricePerUnit(170);
        Product p1 = productDao.get(1);
        o1.setProduct(p1);
        o1.setTrader(trader);
        o1.setQuality(ProductQuality.SCHLECHT);
        trader.getOffers().add(o1);

        traderDao.add(trader);

        trader = traderDao.get(trader.getId());
        assertNotNull(trader);
        assertTrue(trader.getOffers().size() == 1);
    }

}
