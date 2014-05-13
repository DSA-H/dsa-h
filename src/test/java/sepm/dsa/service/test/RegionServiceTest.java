package sepm.dsa.service.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Chris on 12.05.2014.
 */
public class RegionServiceTest {

    private static RegionService rs;
    private static Region region;
    @BeforeClass
    public static void testSetup() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        rs =  (RegionService) ctx.getBean("regionService");

        region = new Region();
        region.setName("testRegion");
        region.setColor("000000");
        region.setTemperature(Temperature.ARCTIC);
        region.setRainfallChance(RainfallChance.LOW);

        System.out.println("testSetup");
    }

    @AfterClass
    public static void testCleanup() {
        // Teardown for data used by the unit tests
    }

    /*@Test(expected = IllegalArgumentException.class)
    public void testExceptionIsThrown() {
    }*/

    @Test
    public void testAdd() {
        int size = rs.getAll().size();
        int id = rs.add(region);

        assertTrue(rs.getAll().size() - 1 == size);
        //TODO: equals is not working right now => DONE
        assertTrue(rs.get(id).equals(region));
        assertEquals(rs.get(id), region);
        rs.remove(region);
    }

    @Test
    public void testRemove() {
        int id = rs.add(region);
        int size = rs.getAll().size();
        rs.remove(region);
        assertTrue(rs.getAll().size() + 1 == size);
    }

    @Test
    public void testUpdate() {
        int id = rs.add(region);
        int size = rs.getAll().size();
        region.setName("testRegion2");
        region.setColor("999999");
        region.setTemperature(Temperature.LOW);

        rs.update(region);
        assertTrue (rs.getAll().size() == size);
        rs.remove(region);
    }
}
