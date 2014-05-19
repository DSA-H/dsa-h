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
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dao.TavernDao;
import sepm.dsa.model.Tavern;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("/testData.xml")
public class TavernDaoTest {

	@Autowired
	private TavernDao tavernDao;

	@Autowired
	private LocationDao locationDao;

	@Test
	public void testAddShouldPersistEntity() throws Exception {
		Tavern tavern = new Tavern();
		tavern.setName("Test Tavern");
		tavern.setLocation(locationDao.get(1));
		tavern.setxPos(42);
		tavern.setyPos(12);
		tavern.setUsage(100);
		tavernDao.add(tavern);

		Tavern persistedTavern = null;
		try {
			persistedTavern = tavernDao.get(tavern.getId());
		} catch (NullPointerException ignored) {}
		assertNotNull("Expected tavern to be persisted", persistedTavern);
	}

	@Test
	public void testUpdate() throws Exception {
		String newTavernName = "FOO Tavern";

		Tavern tavern = tavernDao.get(1);
		tavern.setName(newTavernName);
		tavernDao.update(tavern);

		assertEquals("Expected tavern name to change", newTavernName, tavernDao.get(1).getName());
	}

	@Test
	public void testRemove() throws Exception {
		tavernDao.remove(tavernDao.get(1));

		tavernDao.getAll().
			forEach(tavern -> assertNotSame("Expected to be rid of the tavern", 1, tavern.getId()));
	}

	@Test
	public void testGet() throws Exception {
		Tavern tavern = tavernDao.get(1);
		assertNotNull("Expected a tavern but got null", tavern);
		assertEquals("Expected the retrieving ID", new Integer(1), tavern.getId());
	}

	@Test
	public void testGetAllReturnsTheCorrectAmountOfTaverns() throws Exception {
		List<Tavern> taverns = tavernDao.getAll();
		assertEquals("Expected that size matches fixture count", 2, taverns.size());
	}
}