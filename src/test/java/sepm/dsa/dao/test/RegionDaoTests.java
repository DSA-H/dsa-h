package sepm.dsa.dao.test;

//import com.github.springtestdbunit.DbUnitTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;

/**
 * Created by Michael on 13.05.2014.
 */
@RunWith(JUnit4.class)
@ContextConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class RegionDaoTests {

    private RegionDao regionDao;

    @Before
    public void setUp() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("testContext.xml");
        regionDao = (RegionDao) ctx.getBean("regionDao");
        if (regionDao == null) {
            throw new IllegalStateException("regionDao could not be fetched");
        }
    }

    @After
    public void tearDown() {

    }

    @Test
    @DatabaseSetup("test/resources/testData.xml")
    public void add_shouldPersistEntity() {

        Region region = new Region();
        region.setName("Region1");
        region.setColor("65A3EF");
        region.setComment("comment");
        region.setRainfallChance(RainfallChance.MONSUN);
        region.setTemperature(Temperature.LOW);
        int id = regionDao.add(region);
        Region persistedRegion = regionDao.get(id);
        TestCase.assertTrue(persistedRegion != null);

        regionDao.remove(region);
    }

}