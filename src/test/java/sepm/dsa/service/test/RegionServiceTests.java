package sepm.dsa.service.test;

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
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

/**
 * Created by Michael on 13.05.2014.
 */
@RunWith(JUnit4.class)
//@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class RegionServiceTests {

//    @Autowired
    private RegionService regionService;

    @Before
    public void setUp() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("testContext.xml");
        regionService = (RegionService) ctx.getBean("regionService");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void add_shouldPersistEntry() {
        Region r = new Region();
        r.setName("Region 1");
        r.setColor("424242");
        r.setComment("comment");
        r.setRainfallChance(RainfallChance.MONSUN);
        r.setTemperature(Temperature.LOW);
        
        int id = regionService.add(r);
        TestCase.assertTrue(r.getId() != null);
        Region persistedRegion = regionService.get(id);
        TestCase.assertTrue(persistedRegion != null);

        regionService.remove(r);
    }

}
