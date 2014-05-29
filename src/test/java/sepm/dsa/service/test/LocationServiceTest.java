package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Location;
import sepm.dsa.model.Region;
import sepm.dsa.model.TownSize;
import sepm.dsa.model.Trader;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.RegionService;
import sepm.dsa.service.SaveCancelService;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;

@Transactional
public class LocationServiceTest extends AbstractDatabaseTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TraderDao traderDao;


    @Test
    public void testAdd() throws Exception {
        Location location = new Location();
        location.setComment("foo comment");
        location.setHeight(40);
        location.setName("foo name");
        location.setSize(TownSize.BIG);
        location.setxCoord(5);
        location.setyCoord(10);
        Region someRandomRegion = regionService.get(2);
        location.setRegion(someRandomRegion);
        locationService.add(location);

        Location persistedLocation = locationService.get(location.getId());
        assertTrue(persistedLocation != null);
        locationService.remove(location);
    }

    @Test
    public void testRemove() throws Exception {
        Location location = locationService.get(2);
        locationService.remove(location);
        assertEquals(null, locationService.get(2));
    }

    @Test
    public void testGet() throws Exception {
        Location location = locationService.get(1);
        assertNotNull(location);
    }

    @Test
    public void testGetAll() throws Exception {
        Location l1 = locationService.get(1);
        Location l2 = locationService.get(2);
        assertNotNull("Location with ID=1 must exist in the database for this test", l1);
        assertNotNull("Location with ID=2 must exist in the database for this test", l2);
        assertThat(locationService.getAll(), hasItems(l1, l2));
    }

    @Test
    public void remove_TradersInTown_ShouldCascadeTraderRemoval() {
        Location location = locationService.get(1);
        Collection<Trader> tradersBefore = traderDao.getAllByLocation(location);

        locationService.remove(location);
        getSaveCancelService().save();
        getSaveCancelService().closeSession();
        getSaveCancelService().cancel();
//        saveCancelService.closeSession();

        int tradersForLocationNow = traderDao.getAllByLocation(location).size();

        assertTrue("To run this test reasonably, there must have been at least one trader in the location", tradersBefore.size() > 0);
        for (Trader trader : tradersBefore) {
            assertNull(traderDao.get(trader.getId()));  // assert all traders cascade deleted
        }
        assertEquals(0, tradersForLocationNow);
    }

//    @Test
//    public void update_removesConnections() throws Exception {
//        Location location = locationService.get(4);
//        assertEquals(2, location.getConnections1().size());
//        assertEquals(1, location.getConnections2().size());
//        location.getConnections1().clear();
//        locationService.update(location);
//        Location newLocation = locationService.get(location.getId());
//        assertEquals(0, newLocation.getConnections1().size());
//        assertEquals(1, newLocation.getConnections2().size());
//
//    }
}