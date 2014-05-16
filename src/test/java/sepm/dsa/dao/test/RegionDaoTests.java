package sepm.dsa.dao.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
public class RegionDaoTests {

	@Autowired
	private RegionDao regionDao;

	@Test
	@DatabaseSetup("/testData.xml")
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
	@DatabaseSetup("/testData.xml")
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
