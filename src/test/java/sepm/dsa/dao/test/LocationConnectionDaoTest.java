package sepm.dsa.dao.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationConnectionDao;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.model.Region;
import sepm.dsa.model.TownSize;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.RegionService;

import java.util.List;

import static org.junit.Assert.assertNull;
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
public class LocationConnectionDaoTest extends TestCase {

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
//    @Transactional(readOnly = false)
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml") //todo setup xml file
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
//    @DatabaseSetup("/testData.xml") //todo setup xml file
//    public void remove_shouldRemoveEntity2() throws Exception {
//        int sizeBefore = locationConnectionDao.getAll().size();
//        Location location1 = locationService.get(5);
//        Location location2 = locationService.get(4);    //swapped 1 and 2
//        locationConnection.setLocation1(location1);
//        locationConnection.setLocation2(location2);
//        locationConnectionDao.remove(locationConnection);
//        int sizeNow = locationConnectionDao.getAll().size();
//        assertEquals(sizeBefore - 1, sizeNow);
//        assertNull(locationConnectionDao.get(location1, location2));
//    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldRetrieveEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);
        Location location2 = new Location();
        location2.setId(5);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldRetrieveEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(5);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNotNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldNotFindEntity1() throws Exception {
        Location location1 = new Location();
        location1.setId(4);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(8);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void get_shouldNotFindEntity2() throws Exception {
        Location location1 = new Location();
        location1.setId(8);     // swapped 4 and 5
        Location location2 = new Location();
        location2.setId(4);

        LocationConnection connection = locationConnectionDao.get(location1, location2);
        assertNull(connection);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void getAll_shouldRetrieveEntities() throws Exception {
        List<LocationConnection> allFoundConnections = locationConnectionDao.getAll();
        assertTrue(allFoundConnections.size() >= 3);
    }

}