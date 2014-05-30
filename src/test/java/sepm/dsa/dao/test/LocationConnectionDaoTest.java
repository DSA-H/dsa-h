package sepm.dsa.dao.test;

import org.junit.Before;
import org.junit.Ignore;
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

//	@Ignore("Composite identifier must be fixed")
    @Test
    public void add_shouldPersistEntity() throws Exception {
        int sizeBefore = locationConnectionDao.getAll().size();
        Location location1 = locationService.get(7);
        Location location2 = locationService.get(8);
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);

        locationConnectionDao.add(locationConnection);

        getSaveCancelService().save();

        int sizeAfter = locationConnectionDao.getAll().size();
        assertEquals(sizeBefore + 1, sizeAfter);
    }

//	@Ignore("Composite identifier must be fixed")
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
        assertEquals(null, locationConnectionDao.get(new LocationConnection.Pk(location1, location2)));
    }

    @Test
    public void remove_shouldRemoveEntity2() throws Exception {
        int sizeBefore = locationConnectionDao.getAll().size();
        Location location1 = locationService.get(5);
        Location location2 = locationService.get(4);    // swapped 4 and 5
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);
        locationConnectionDao.remove(locationConnection);
        int sizeNow = locationConnectionDao.getAll().size();
        assertEquals(sizeBefore - 1, sizeNow);
        assertEquals(null, locationConnectionDao.get(new LocationConnection.Pk(location1, location2)));
    }

//	@Ignore("Composite identifier must be fixed")
    @Test
    public void get_shouldRetrieveEntity1() throws Exception {
        Location location1 = locationService.get(4);
        Location location2 = locationService.get(5);

        LocationConnection.Pk pk = new LocationConnection.Pk(location1, location2);
        LocationConnection connection = locationConnectionDao.get(pk);
        assertNotNull(connection);
    }

//	@Ignore("Composite identifier must be fixed")
    @Test
    public void get_shouldRetrieveEntity2() throws Exception {
        Location location1 = locationService.get(5);
        Location location2 = locationService.get(4); // swapped 4 and 5

        LocationConnection.Pk pk = new LocationConnection.Pk(location1, location2);
        LocationConnection connection = locationConnectionDao.get(pk);
        assertNotNull(connection);
    }

//	@Ignore("Composite identifier must be fixed")
    @Test
    public void get_shouldNotFindEntity1() throws Exception {
        Location location1 = locationService.get(4);
        Location location2 = locationService.get(8);

        LocationConnection.Pk pk = new LocationConnection.Pk(location1, location2);
        LocationConnection connection = locationConnectionDao.get(pk);
        assertNull(connection);
    }

//	@Ignore("Composite identifier must be fixed")
    @Test
    public void get_shouldNotFindEntity2() throws Exception {
        Location location1 = locationService.get(8);
        Location location2 = locationService.get(4);    // swapped 4 and 8

        LocationConnection.Pk pk = new LocationConnection.Pk(location1, location2);
        LocationConnection connection = locationConnectionDao.get(pk);
        assertNull(connection);
    }

//	@Ignore("Composite identifier must be fixed")
    @Test
    public void getAll_shouldRetrieveEntities() throws Exception {
        List<LocationConnection> allFoundConnections = locationConnectionDao.getAll();
        assertTrue(allFoundConnections.size() >= 3);
    }
}