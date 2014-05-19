package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.model.Region;
import sepm.dsa.model.TownSize;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.RegionService;
import sepm.dsa.service.RegionServiceImpl;

import javax.validation.constraints.AssertTrue;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class LocationServiceTest extends TestCase {

    @Autowired
    private LocationService locationService;

    @Autowired
    private RegionService regionService;

    private final static double EPSILON = 1E-5;

    @Test
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml") //todo setup xml file
    public void testRemove() throws Exception {
        Location location = locationService.get(2);
        locationService.remove(location);

        //TODO oder sollte das eine Exception sein?? -- Antwort Michael: nein, keine Exception
        assertEquals(null, locationService.get(2));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() throws Exception {
        Location location = locationService.get(2);
        assertNotNull(location);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        List<Location> allFoundLocations = locationService.getAll();
        Location l1 = locationService.get(1);
        Location l2 = locationService.get(2);
        assertThat(locationService.getAll(), hasItems(l1, l2));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_locationsAround() throws Exception {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationService.suggestLocationConnectionsAround(location, 100);
        assertEquals(1, suggestions.size());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_noLocationsAround() throws Exception {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationService.suggestLocationConnectionsAround(location, 25);
        assertEquals(0, suggestions.size());
    }



    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestConnectionsAround_locationIsLocation1() {
        Location location = locationService.get(4);
        List<LocationConnection> suggestions = locationService.suggestLocationConnectionsAround(location, 100);
        LocationConnection con1 = suggestions.get(0);
        assertEquals(location, con1.getLocation1());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestedDistanceBetween_positive() {
        Location location = locationService.get(6);
        List<LocationConnection> suggestions = locationService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            Location other = suggestion.getLocation2();
            double distanceSuggested = locationService.suggestedDistanceBetween(location, other);
            assertTrue(distanceSuggested >= 0);
        }
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void suggestedDistanceBetween_GtOrEqDirectDistance() {
        Location location = locationService.get(6);
        List<LocationConnection> suggestions = locationService.suggestLocationConnectionsAround(location, 100);
        for (LocationConnection suggestion : suggestions) {
            Location other = suggestion.getLocation2();
            double distanceSuggested = locationService.suggestedDistanceBetween(location, other);
            assertTrue(distanceSuggested >= Math.abs(location.getxCoord() - other.getxCoord()));
            assertTrue(distanceSuggested >= Math.abs(location.getyCoord() - other.getyCoord()));
            double minDistance = Math.sqrt(Math.pow(location.getxCoord() - other.getxCoord(), 2)
                                            + Math.pow(location.getyCoord() - other.getyCoord(), 2));
            assertTrue(distanceSuggested >= minDistance - EPSILON);
        }
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void update_removesConnections() throws Exception {
        Location location = locationService.get(4);
        assertEquals(2, location.getConnections1().size());
        assertEquals(1, location.getConnections2().size());
        location.getConnections1().clear();
        locationService.update(location);
        Location newLocation = locationService.get(location.getId());
        assertEquals(0, newLocation.getConnections1().size());
        assertEquals(1, newLocation.getConnections2().size());

    }
}