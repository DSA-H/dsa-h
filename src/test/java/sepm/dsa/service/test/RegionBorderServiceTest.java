package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sepm.dsa.model.*;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class RegionBorderServiceTest {

    @Autowired
    private RegionBorderService rbs;

    @Autowired
    private RegionService rs;

    private RegionBorder regionBorder;
    private RegionBorderPk regionBorderPK;

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

        regionBorderPK = new RegionBorderPk();
        regionBorderPK.setRegion1(r1);
        regionBorderPK.setRegion2(r2);

        regionBorder = new RegionBorder();
        regionBorder.setBorderCost(1);
        regionBorder.setPk(regionBorderPK);

        rs.add(r1);
        rs.add(r2);
    }

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