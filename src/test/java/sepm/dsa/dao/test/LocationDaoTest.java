package sepm.dsa.dao.test;

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
import sepm.dsa.dao.LocationDao;
import sepm.dsa.model.Location;
import sepm.dsa.model.Region;
import sepm.dsa.service.RegionService;
import sepm.dsa.service.RegionServiceImpl;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class LocationDaoTest extends TestCase {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private RegionService regionService;

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        Location location = new Location();
        location.setComment("foo comment");
        location.setHeight(40);
        location.setName("foo name");
        Region someRandomRegion = regionService.get(2);
        location.setRegion(someRandomRegion);
        locationDao.add(location);

        Location persistedLocation = locationDao.get(location.getId());
        assertTrue(persistedLocation != null);
        locationDao.remove(location);
    }

    @Test
    @DatabaseSetup("/testData.xml") //todo setup xml file
    public void testRemove() throws Exception {
        Location location = locationDao.get(2);
        locationDao.remove(location);

        //TODO oder sollte das eine Exception sein??
        assertEquals(null,locationDao.get(location.getId()));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() throws Exception {
        Location location = locationDao.get(2);
        assertNotNull(location);
    }
    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        List<Location> allFoundLocations = locationDao.getAll();
        Location l1 = locationDao.get(1);
        Location l2 = locationDao.get(2);
        assertThat(locationDao.getAll(), hasItems(l1, l2));
    }
}