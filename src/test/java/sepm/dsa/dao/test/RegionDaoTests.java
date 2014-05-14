package sepm.dsa.dao.test;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;

/**
 * Created by Michael on 13.05.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class RegionDaoTests {

    @Autowired
    private RegionDao regionDao;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void add_shouldPersistEntity() {

        Region region = new Region();
        region.setName("Region1");
        region.setColor("65A3EF");
        region.setTemperature(Temperature.MEDIUM);
        region.setRainfallChance(RainfallChance.MONSUN);
        int id = regionDao.add(region);
        Region persistedRegion = regionDao.get(id);
        TestCase.assertTrue(persistedRegion != null);

        regionDao.remove(region);
    }

}
