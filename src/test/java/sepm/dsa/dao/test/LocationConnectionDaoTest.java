package sepm.dsa.dao.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.LocationConnectionDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.LocationService;

import java.util.List;

import static org.junit.Assert.*;

public class LocationConnectionDaoTest extends AbstractDatabaseTest {

    @Autowired
    private LocationConnectionDao locationConnectionDao;

    @Autowired
    private LocationService locationService;

    LocationConnection locationConnection;

    @Before
    public void setUp() {
        locationConnection = new LocationConnection();
    }

    @Test
    public void add_shouldPersistEntity() throws Exception {
        int sizeBefore = locationConnectionDao.getAll().size();
        Location location1 = locationService.get(7);
        Location location2 = locationService.get(8);
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);

        locationConnectionDao.add(locationConnection);

        int sizeAfter = locationConnectionDao.getAll().size();
        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    public void remove_shouldRemoveEntity1() throws Exception {
        int sizeBefore = locationConnectionDao.getAll().size();
        Location location1 = locationService.get(4);
        Location location2 = locationService.get(5);
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);
        locationConnectionDao.remove(locationConnection);
        int sizeNow = locationConnectionDao.getAll().size();
        assertEquals(sizeBefore - 1, sizeNow);
        assertEquals(null, locationConnectionDao.get(location1, location2));
    }

//    @Test
//    public void remove_shouldRemoveEntity2() throws Exception {
//        int sizeBefore = locationConnectionDao.getAll().size();
//        Location location1 = locationService.get(5);
//        Location location2 = locationService.get(4);    //swapped 1 and 2
//        locationConnection.setLocation1(location1);
//        locationConnection.setLocation2(location2);
//        locationConnectionDao.removeConnection(locationConnection);
//        int sizeNow = locationConnectionDao.getAll().size();
//        assertEquals(sizeBefore - 1, sizeNow);
//        assertNull(locationConnectionDao.get(location1, location2));
//    }

    @Test
    public void get_shouldRetrieveEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);
        Location location2 = new Location();
        location2.setId(5);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    public void get_shouldRetrieveEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(5);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    public void get_shouldNotFindEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(8);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNull(connection);
    }

    @Test
    public void get_shouldNotFindEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(8);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNull(connection);
    }

    @Test
    public void getAll_shouldRetrieveEntities() throws Exception {
        List<LocationConnection> allFoundConnections = locationConnectionDao.getAll();
        assertTrue(allFoundConnections.size() >= 3);
    }
}