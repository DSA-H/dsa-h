package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegionDaoTest extends AbstractDatabaseTest {

	@Autowired
	private RegionDao regionDao;

	@Test
	public void add_shouldPersistEntity() {
		Region region = new Region();
		region.setName("Region1");
		region.setColor("65A3EF");
		region.setComment("comment");
		region.setRainfallChance(RainfallChance.MONSUN);
		region.setTemperature(Temperature.LOW);
		regionDao.add(region);
		Region persistedRegion = regionDao.get(region.getId());
		assertTrue(persistedRegion != null);

		regionDao.remove(region);
	}

	@Test
	public void testGetRegionReturnsRegionFromDatabase() {
		Region r = regionDao.get(2);
		assertEquals(new Integer(2), r.getId());
	}

    @Test(expected = org.hibernate.PropertyValueException.class)
    public void add_incompleteRegion_shouldNOTPersistEntity() {
        Region region = new Region();
        region.setName("Region1");
        region.setComment("comment");
        region.setRainfallChance(RainfallChance.MONSUN);
        regionDao.add(region);
        Region persistedRegion = regionDao.get(region.getId());
        assertTrue(persistedRegion == null);
    }
}
