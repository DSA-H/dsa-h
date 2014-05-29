package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.RegionService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegionServiceTest extends AbstractDatabaseTest {

	@Autowired
	private RegionService rs;

    @Autowired
    private LocationService locationService;

    private Region addRegion;
    private Region deleteRegion;
    private Region updateRegion;

    private Region addRegion2;
    private Region addRegion3;
//    private RegionBorder regionBorder1;

    @Before
    public void setup() {

        //TODO: Import directly into testDB
        addRegion = new Region();
        addRegion.setName("testRegionAdd");
        addRegion.setColor("000000");
        addRegion.setComment("comment");
        addRegion.setTemperature(Temperature.ARCTIC);
        addRegion.setRainfallChance(RainfallChance.LOW);

        deleteRegion = new Region();
        deleteRegion.setName("testRegionDelete");
        deleteRegion.setColor("000000");
        deleteRegion.setComment("comment");
        deleteRegion.setTemperature(Temperature.HIGH);
        deleteRegion.setRainfallChance(RainfallChance.MEDIUM);
        rs.add(deleteRegion);

        updateRegion = new Region();
        updateRegion.setName("testRegionUpdate");
        updateRegion.setColor("000000");
        updateRegion.setComment("comment");
        updateRegion.setTemperature(Temperature.MEDIUM);
        updateRegion.setRainfallChance(RainfallChance.HIGH);
        rs.add(updateRegion);

        addRegion2 = new Region();
        addRegion2.setName("testRegionAdd 2");
        addRegion2.setColor("222222");
        addRegion2.setComment("comment 2");
        addRegion2.setTemperature(Temperature.VULCANO);
        addRegion2.setRainfallChance(RainfallChance.HIGH);

        addRegion3 = new Region();
        addRegion3.setName("testRegionAdd 2");
        addRegion3.setColor("222222");
        addRegion3.setComment("comment 2");
        addRegion3.setTemperature(Temperature.VULCANO);
        addRegion3.setRainfallChance(RainfallChance.HIGH);

//        regionBorder1 = new RegionBorder();
//        regionBorder1.setBorderCost(8);
//        regionBorder1.setRegion1(addRegion2);
//        regionBorder1.setRegion2(addRegion3);
//        addRegion2.getBorders1().addConnection(regionBorder1);
//        addRegion3.getBorders2().addConnection(regionBorder1);

	    saveCancelService.save();
    }

    @Test
    public void testAdd() {
        int size = rs.getAll().size();
        rs.add(addRegion);

	    saveCancelService.save();

        assertEquals(size + 1, rs.getAll().size());
        //TODO: equals is not working right now => DONE
        assertTrue(rs.get(addRegion.getId()).equals(addRegion));
        assertEquals(rs.get(addRegion.getId()), addRegion);
    }

    @Test
    public void testRemove() {
        int size = rs.getAll().size();
        rs.remove(deleteRegion);

	    saveCancelService.save();

        assertEquals(size - 1, rs.getAll().size());
    }

    @Test
    public void testUpdate() {
        int size = rs.getAll().size();
        updateRegion.setName("testRegion2");
        updateRegion.setColor("999999");
        updateRegion.setComment("comment");
        updateRegion.setTemperature(Temperature.LOW);

        rs.update(updateRegion);
        assertTrue (rs.getAll().size() == size);
    }

    @Test
    public void add_withBorder_shouldPersist() {
        int size = rs.getAll().size();

        // must exist
        rs.add(addRegion2);

        // addConnection region border
        RegionBorder regionBorder1 = new RegionBorder();
        regionBorder1.setBorderCost(8);
        addRegion3.getBorders2().add(regionBorder1);
        regionBorder1.setRegion1(addRegion2);
        regionBorder1.setRegion2(addRegion3);

        // now addConnection the region
        rs.add(addRegion3);

	    saveCancelService.save();
	    saveCancelService.closeSession();

        List<Region> listLater = rs.getAll();
        int sizeLater = listLater.size();
        assertEquals(size + 2, sizeLater);
        assertTrue(listLater.contains(addRegion2));
        assertTrue(listLater.contains(addRegion3));
        addRegion2 = rs.get(addRegion2.getId());
        assertTrue(addRegion2.getAllBorders().contains(regionBorder1));
        addRegion3 = rs.get(addRegion3.getId());
        assertTrue(addRegion3.getAllBorders().contains(regionBorder1));

    }

    @Test
    public void oneToMany_hasValues() {
        Region region = rs.get(1);
        assertTrue(region.getAllBorders().size() == 3);
        assertTrue(region.getBorders1().size() == 3);
        assertTrue(region.getBorders2().size() == 0);

    }

    @Test
    public void remove_LocationsInside_ShouldCascadeDeleteLocations() {
        Region region = rs.get(1);
        List<Location> locationsInside = locationService.getAllByRegion(region.getId());
        int totalLocations = locationService.getAll().size();

        rs.remove(region);
	    saveCancelService.save();

        List<Location> locationsInsideNow = locationService.getAllByRegion(region.getId());
        int totalLocationsNow = locationService.getAll().size();


        assertTrue("Must have at least one location inside to get a reasonable test result", locationsInside.size() > 0);
        assertEquals(totalLocations - 1, totalLocationsNow);
        assertEquals(0, locationsInsideNow.size());

    }

}
