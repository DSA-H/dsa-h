package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Location;
import sepm.dsa.model.Region;
import sepm.dsa.model.TownSize;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.RegionService;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class LocationDaoTest extends AbstractDatabaseTest {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RegionService regionService;

    @Test
    public void testAdd() throws Exception {
        List<Location> all = locationDao.getAll();
        Location location = new Location();
        location.setComment("foo comment");
        location.setHeight(40);
        location.setName("foo name");
        location.setSize(TownSize.BIG);
        location.setxCoord(5);
        location.setyCoord(10);
        Region someRandomRegion = regionService.get(2);
        location.setRegion(someRandomRegion);
        locationDao.add(location);

        Location persistedLocation = locationDao.get(location.getId());
        assertNotNull(persistedLocation);
        locationDao.remove(location);
    }

    @Test
    public void testRemove() throws Exception {
        Location location = locationDao.get(2);
        locationDao.remove(location);

        assertNull(locationDao.get(location.getId()));
    }

    @Test
    public void testGet() throws Exception {
        Location location = locationDao.get(2);
        assertNotNull(location);
    }
    @Test
    public void testGetAll() throws Exception {
        List<Location> allFoundLocations = locationDao.getAll();
        Location l1 = locationDao.get(1);
        Location l2 = locationDao.get(2);
        assertThat(locationDao.getAll(), hasItems(l1, l2));
    }

//    @Test
//    public void update_removesConnections() throws Exception {
//        Location location = locationDao.get(4);
//        location.getConnections1().remove(location.getConnections1().iterator().next());
//        location.getConnections2().clear();
//        locationDao.update(location);
//        Location newLocation = locationDao.get(location.getId());
//        assertEquals(1, newLocation.getConnections1().size());
//        assertEquals(0, newLocation.getConnections2().size());
//
//    }

    @Test
    public void getAllAround_SomeLocationsAround() throws Exception {
        Location location = locationDao.get(4);
        List<Location> locationsAround = locationDao.getAllAround(location, 100.0);
        Location l1 = locationDao.get(5);
        Location l2 = locationDao.get(6);
        Location l3 = locationDao.get(7);
        Location l4 = locationDao.get(8);
        assertThat(locationsAround, hasItems(l1, l2, l3, l4));
        assertEquals(4, locationsAround.size());
    }

    @Test
    public void getAllAround_NoLocationsAround() throws Exception {
        Location location = locationDao.get(4);
        List<Location> locationsAround = locationDao.getAllAround(location, 10.0);
        assertTrue(locationsAround.size() == 0);
    }

    @Test
    public void getAllAroundNotConnected_NoConnectionsEqualsgetAllAround() throws Exception {
        Location location = locationDao.get(8);
        int sizeAll = locationDao.getAllAround(location, 50.0).size();
        int sizeAllNotConnected = locationDao.getAllAroundNotConnected(location, 50.0).size();
        assertEquals(sizeAll, sizeAllNotConnected);
    }

    @Test
    public void getAllAroundNotConnected_ConnectedLocationsNotReturned() throws Exception {
        Location location = locationDao.get(7);
        int sizeAll = locationDao.getAllAround(location, 50.0).size();
        int sizeAllNotConnected = locationDao.getAllAroundNotConnected(location, 50.0).size();
        assertEquals(sizeAll - 1, sizeAllNotConnected);
    }

    @Test
    public void getAllByNameNotConnectedTo_UnconnectedAndUnfilteredReturnsAll() throws Exception {
        Location location = locationDao.get(8);
        int sizeAll = locationDao.getAll().size();
        int sizeAllNotConnected = locationDao.getAllByNameNotConnectedTo(location, "%").size();
        assertEquals(sizeAll - 1, sizeAllNotConnected);
    }

    @Test
    public void getAllByNameNotConnectedTo_UnconnectedAndFiltered() throws Exception {
        Location location = locationDao.get(8);
        int sizeAllNotConnected = locationDao.getAllByNameNotConnectedTo(location, "%_LoConTest%").size();
        assertEquals(5, sizeAllNotConnected);
    }

    @Test
    public void getAllByNameNotConnectedTo_ConnectedAndFiltered() throws Exception {
        Location location = locationDao.get(7);
        int sizeAllNotConnected = locationDao.getAllByNameNotConnectedTo(location, "%_LoConTest%").size();
        assertEquals(4, sizeAllNotConnected);
    }
}