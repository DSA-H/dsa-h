package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Location;
import sepm.dsa.model.Region;
import sepm.dsa.model.TownSize;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.LocationServiceImpl;
import sepm.dsa.service.TraderService;

import static org.mockito.Mockito.*;


public class IsolatedLocationServiceTest extends AbstractDatabaseTest {

    private LocationService isolatedLocationService;
    private LocationDao locationDaoMock;
    private TraderService traderServiceMock;

    private Location location1;

    @Before
    public void setUp() {
        location1 = new Location();
        location1.setId(1);
        location1.setName("Location1");
        location1.setComment("foo comment");
        location1.setHeight(40);
        location1.setSize(TownSize.BIG);
        location1.setxCoord(5);
        location1.setyCoord(10);
        Region region = new Region();
        region.setId(1);
        location1.setRegion(region);

        locationDaoMock = mock(LocationDao.class);
        when(locationDaoMock.get(1)).thenReturn(location1);

        traderServiceMock = mock(TraderService.class);

        LocationServiceImpl locationServiceImpl = new LocationServiceImpl();
        locationServiceImpl.setLocationDao(locationDaoMock);
        locationServiceImpl.setTraderService(traderServiceMock);
        isolatedLocationService = locationServiceImpl;
    }

    @Test
     public void add_shouldCallDaoAdd() throws Exception {
        Location location = new Location();
        location.setName("foo name");
        location.setComment("foo comment");
        location.setHeight(40);
        location.setSize(TownSize.BIG);
        location.setxCoord(5);
        location.setyCoord(10);
        Region region = new Region();
        region.setId(2);//regionService.get(2);
        location.setRegion(region);
        isolatedLocationService.add(location);
        verify(locationDaoMock).add(any(Location.class));
    }

    @Test(expected = DSAValidationException.class)
    public void add_invalidLocation_shouldThrowException() throws Exception {
        Location location = new Location();
        location.setName(null); // invalid!!
        location.setComment("foo comment");
        location.setHeight(40);
        location.setSize(TownSize.BIG);
        location.setxCoord(5);
        location.setyCoord(10);
        Region region = new Region();
        region.setId(2);
        location.setRegion(region);
        isolatedLocationService.add(location);
    }


    @Test
    public void update_shouldCallDaoUpdate() throws Exception {
        Location location = locationDaoMock.get(1);
        location.setName(location.getName() + "_changed");
        location.setxCoord(location.getxCoord() + 15);
        isolatedLocationService.update(location);
        verify(locationDaoMock).update(any(Location.class));
    }

    @Test(expected = DSAValidationException.class)
    public void update_invalidLocation_shouldThrowException() throws Exception {
        Location location = locationDaoMock.get(1);
        location.setName(null);
        location.setxCoord(location.getxCoord() + 15);
        isolatedLocationService.update(location);
    }

    @Test
    public void remove_shouldCallDaoRemove() throws Exception {
        Location location = locationDaoMock.get(1);
        isolatedLocationService.remove(location);
        verify(locationDaoMock).remove(any(Location.class));
    }

    @Test
    public void get_shouldCallDaoGet() {
        isolatedLocationService.get(1);
        verify(locationDaoMock).get(1);
    }

    @Test
    public void getAll_shouldCallDaoGetAll() {
        isolatedLocationService.getAll();
        verify(locationDaoMock).getAll();
    }

    @Test
    public void getAllByRegion_shouldCallDaoGetAllByRegion() {
        isolatedLocationService.getAllByRegion(1);
        verify(locationDaoMock).getAllByRegion(1);
    }

}