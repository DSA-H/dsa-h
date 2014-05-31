package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RegionBorderServiceTest extends AbstractDatabaseTest {

    @Autowired
    private RegionBorderService regionBorderService;

    @Autowired
    private RegionService regionService;


    @Test
    public void testAdd() {

        RegionBorder regionBorder = new RegionBorder();
        regionBorder.setBorderCost(1);
        regionBorder.setRegion1(regionService.get(2));
        regionBorder.setRegion2(regionService.get(3));

        int size = regionBorderService.getAll().size();
        regionBorderService.add(regionBorder);

        getSaveCancelService().save();

        assertEquals(size + 1, regionBorderService.getAll().size());
        assertNotNull(regionBorderService.get(regionBorder.getRegion1(), regionBorder.getRegion2()));
    }

    @Test
    public void testRemove() {

        Region region1 = regionService.get(1);
        Region region2 = regionService.get(2);

        RegionBorder regionBorder = regionBorderService.get(region1, region2);

        int size = regionBorderService.getAll().size();
        regionBorderService.remove(regionBorder);
        getSaveCancelService().save();

        assertEquals(size - 1, regionBorderService.getAll().size());
        assertNull(regionBorderService.get(region1, region2));
    }

    @Test
    public void testUpdate() {

        RegionBorder regionBorder = regionBorderService.get(regionService.get(1), regionService.get(2));

        int size = regionBorderService.getAll().size();
        regionBorder.setBorderCost(120);

        regionBorderService.update(regionBorder);
        getSaveCancelService().save();

        assertEquals(regionBorderService.get(regionBorder.getRegion1(), regionBorder.getRegion2()).getBorderCost(),
                new Integer(120));

    }
}