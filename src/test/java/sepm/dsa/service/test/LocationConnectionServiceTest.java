package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.TestCase;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationConnectionDao;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.LocationConnectionService;
import sepm.dsa.service.LocationService;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@Transactional
public class LocationConnectionServiceTest extends TestCase {

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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
    public void get_shouldRetrieveEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);
        Location location2 = new Location();
        location2.setId(5);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldRetrieveEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(5);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldNotFindEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(8);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldNotFindEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(8);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionService.get(location1, location2);
        assertNull(connection);
    }


    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_locationsAround() throws Exception {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        assertEquals(1, suggestions.size());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_noLocationsAround() throws Exception {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 0);
        assertEquals(0, suggestions.size());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_locationsAreNotEqual() {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            assertNotSame(suggestion.getLocation1(), suggestion.getLocation2());
        }
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_locationIsLocation1() {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationConnectionService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            assertEquals(location, suggestion.getLocation1());
        }
    }

    @Test
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
    public void suggestLocationConnectionsByFilter_nullFilterReturnsAllButLocationItself() {
        Location location = locationService.get(8); // 8 does not have locations connected
        int sizeUnfiltered = locationConnectionService.suggestLocationConnectionsByFilter(location, null).size();
        int sizeAll = locationDao.getAll().size();
        assertEquals(sizeAll - 1, sizeUnfiltered);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestLocationConnectionsByFilter_returnsFilteredByName() {
        Location location = locationService.get(8); // 8 does not have locations connected
        List<LocationConnection> conn = locationConnectionService.suggestLocationConnectionsByFilter(location, "_LoConTest");
        assertEquals(5, conn.size());
    }


}