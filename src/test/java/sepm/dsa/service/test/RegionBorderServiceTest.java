package sepm.dsa.service.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sepm.dsa.model.*;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Chris on 12.05.2014.
 */
public class RegionBorderServiceTest {
    private static RegionBorderService rbs;
    private static RegionBorder regionBorder;
    private static RegionBorderPk regionBorderPK;

    @BeforeClass
    public static void testSetup() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        rbs = (RegionBorderService) ctx.getBean("regionBorderService");
        RegionService rs = (RegionService) ctx.getBean("regionService");

        Region r1 = new Region();
        Region r2 = new Region();
        r1.setColor("000000");
        r2.setColor("999999");
        r1.setName("r1");
        r2.setName("r2");
	    r1.setRainfallChance(RainfallChance.HIGH);
	    r2.setRainfallChance(RainfallChance.HIGH);
	    r1.setTemperature(Temperature.ARCTIC);
	    r2.setTemperature(Temperature.ARCTIC);

        regionBorderPK = new RegionBorderPk();
        regionBorderPK.setRegion1(r1);
        regionBorderPK.setRegion2(r2);

        regionBorder = new RegionBorder();
        regionBorder.setBorderCost(1);
        regionBorder.setPk(regionBorderPK);

        rs.add(r1);
        rs.add(r2);
    }

    @AfterClass
    public static void testCleanup() {
        // Teardown for data used by the unit tests
    }

    /*
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionIsThrown() {
    }
    */

    @Test
    public void testAdd() {
        int size = rbs.getAll().size();
        RegionBorderPk rbpk2 = rbs.add(regionBorder);

        assertTrue(rbs.getAll().size() - 1 == size);

        assertTrue(rbs.get(rbpk2).equals(regionBorder));
        assertEquals(rbs.get(rbpk2), regionBorder);
        rbs.remove(regionBorder);
    }

    @Test
    public void testRemove() {
        RegionBorderPk rbpk2 = rbs.add(regionBorder);
        int size = rbs.getAll().size();
        rbs.remove(regionBorder);
        assertTrue(rbs.getAll().size() + 1 == size);
    }

    @Test
    public void testUpdate() {
        RegionBorderPk rbpk2 = rbs.add(regionBorder);
        int size = rbs.getAll().size();
        regionBorder.setBorderCost(2);

        rbs.update(regionBorder);
        assertTrue (rbs.getAll().size() == size);
        rbs.remove(regionBorder);
    }
}