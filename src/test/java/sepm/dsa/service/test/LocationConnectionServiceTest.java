package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.LocationConnectionService;
import sepm.dsa.service.LocationService;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class LocationConnectionServiceTest extends AbstractDatabaseTest {

    @Autowired
    private LocationConnectionService locationConnectionService;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private LocationService locationService;

    LocationConnection locationConnection;

    private final static double EPSILON = 1E-5;


    @Before
    public void setUp() {
        locationConnection = new LocationConnection();
    }

    @Test
    public void add_shouldPersistEntity() throws Exception {
        Location location1 = locationService.get(7);
        Location location2 = locationService.get(8);
        locationConnection = new LocationConnection();
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);

        locationConnectionService.add(locationConnection);

        assertNotNull(locationConnectionService.get(location1, location2));
    }

    @Test
    public void remove_shouldRemoveEntity1() throws Exception {
        Location location1 = locationService.get(4);
        Location location2 = locationService.get(5);
        locationConnection = new LocationConnection();
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);
        locationConnectionService.remove(locationConnection);
        assertNull(locationConnectionService.get(location1, location2));
    }


    @Test
    public void remove_shouldRemoveEntity2() throws Exception {
        Location location1 = locationService.get(5);
        Location location2 = locationService.get(4); // swapped 4 and 5
        locationConnection = new LocationConnection();
        locationConnection.setLocation1(location1);
        locationConnection.setLocation2(location2);
        locationConnection.setTravelTime(5);
        locationConnectionService.remove(locationConnection);
        assertNull(locationConnectionService.get(location1, location2));
    }

    @Test
    public void get_shouldRetrieveEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);
        Location location2 = new Location();
        location2.setId(5);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    public void get_shouldRetrieveEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(5);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    public void get_shouldNotFindEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(8);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNull(connection);
    }

    @Test
    public void get_shouldNotFindEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(8);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNull(connection);
    }


    @Test
    public void suggestConnectionsAround_locationsAround() throws Exception {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        assertEquals(1, suggestions.size());
    }

    @Test
    public void suggestConnectionsAround_noLocationsAround() throws Exception {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 0);
        assertEquals(0, suggestions.size());
    }

    @Test
    public void suggestConnectionsAround_locationsAreNotEqual() {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            assertNotSame(suggestion.getLocation1(), suggestion.getLocation2());
        }
    }

    @Test
    public void suggestConnectionsAround_locationIsLocation1() {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            assertEquals(location, suggestion.getLocation1());
        }
    }

    @Test
    public void suggestedDistanceBetween_positive() {
        Location location = locationService.get(6);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            Location other = suggestion.getLocation2();
            double distanceSuggested = locationConnectionService.suggestedDistanceBetween(location, other);
            assertTrue(distanceSuggested >= 0);
        }
    }

    @Test
    public void suggestedDistanceBetween_GtOrEqDirectDistance() {
        Location location = locationService.get(6);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            Location other = suggestion.getLocation2();
            double distanceSuggested = locationConnectionService.suggestedDistanceBetween(location, other);
            assertTrue(distanceSuggested >= Math.abs(location.getxCoord() - other.getxCoord()));
            assertTrue(distanceSuggested >= Math.abs(location.getyCoord() - other.getyCoord()));
            double minDistance = Math.sqrt(Math.pow(location.getxCoord() - other.getxCoord(), 2)
                    + Math.pow(location.getyCoord() - other.getyCoord(), 2));
            assertTrue(distanceSuggested >= minDistance - EPSILON);
        }
    }

    @Test
    public void suggestLocationConnectionsByFilter_nullFilterReturnsAllButLocationItself() {
        Location location = locationService.get(8); // 8 does not have locations connected
        int sizeUnfiltered = locationConnectionService.suggestLocationConnectionsByFilter(location, null).size();
        int sizeAll = locationDao.getAll().size();
        assertEquals(sizeAll - 1, sizeUnfiltered);
    }

    @Test
    public void suggestLocationConnectionsByFilter_returnsFilteredByName() {
        Location location = locationService.get(8); // 8 does not have locations connected
        List<LocationConnection> conn = locationConnectionService.suggestLocationConnectionsByFilter(location, "_LoConTest");
        assertEquals(5, conn.size());
    }
}