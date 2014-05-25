package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegionBorderServiceTest extends AbstractDatabaseTest {

    @Autowired
    private RegionBorderService rbs;

    @Autowired
    private RegionService rs;

    private RegionBorder regionBorder;

    @Before
    public void testSetup() {
        Region r1 = new Region();
        Region r2 = new Region();
        r1.setColor("000000");
        r2.setColor("999999");
        r1.setName("r1");
        r2.setName("r2");
        r1.setComment("comment");
        r2.setComment("comment");
	    r1.setRainfallChance(RainfallChance.HIGH);
	    r2.setRainfallChance(RainfallChance.HIGH);
	    r1.setTemperature(Temperature.ARCTIC);
	    r2.setTemperature(Temperature.ARCTIC);

        regionBorder = new RegionBorder();
        regionBorder.setBorderCost(1);
        regionBorder.setRegion1(r1);
        regionBorder.setRegion2(r2);

        rs.add(r1);
        rs.add(r2);
    }

    @Test
    public void testAdd() {
        int size = rbs.getAll().size();
        rbs.add(regionBorder);

        assertTrue(rbs.getAll().size() - 1 == size);

//        assertEquals(rbs.get(regionBorder.getPk()), regionBorder);
        rbs.remove(regionBorder);
    }

    @Test
    public void testRemove() {
        rbs.add(regionBorder);
        int size = rbs.getAll().size();
        rbs.remove(regionBorder);
        assertTrue(rbs.getAll().size() + 1 == size);
    }

    @Test
    public void testUpdate() {
        rbs.add(regionBorder);
        int size = rbs.getAll().size();
        regionBorder.setBorderCost(2);

        rbs.update(regionBorder);
        assertTrue (rbs.getAll().size() == size);
        rbs.remove(regionBorder);
    }
}