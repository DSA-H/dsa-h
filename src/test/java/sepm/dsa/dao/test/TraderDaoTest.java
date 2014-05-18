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
        trader.setCategory();
        trader.setCh(10);
        trader.setIn(11);
        trader.setMu(14);
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

}
