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
import sepm.dsa.dao.PlayerDao;
import sepm.dsa.model.Player;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
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
public class PlayerDaoTest {

	@Autowired
	private PlayerDao playerDao;

	@Autowired
	private LocationDao locationDao;

	@Test
	public void testAddShouldPersistEntity() throws Exception {
		Player player = new Player();
		player.setName("Test Player");
		player.setComment("Comment");
		playerDao.add(player);

		Player persistedPlayer = playerDao.get(player.getId());
		assertNotNull("Expected player to be persisted", persistedPlayer);
//        assertTrue(false, "todo impl"); //TODO
	}

	@Test
	public void testUpdate() throws Exception {
		String newPlayerName = "Test Subject 1";

		Player player = playerDao.get(1);
		player.setName(newPlayerName);
		playerDao.update(player);

		assertEquals("Expected player name to change", newPlayerName, playerDao.get(1).getName());
	}

	@Test
	public void testRemove() throws Exception {
		playerDao.remove(playerDao.get(1));

		playerDao.getAll().
			forEach(player -> assertNotSame("Expected to be rid of the player", 1, player.getId()));
	}

	@Test
	public void testGet() throws Exception {
		Player player = playerDao.get(1);
		assertNotNull("Expected a player but got null", player);
		assertEquals("Expected the retrieving ID", new Integer(1), player.getId());
	}

	@Test
	public void testGetAllReturnsTheCorrectAmountOfPlayers() throws Exception {
		List<Player> players = playerDao.getAll();
		assertEquals("Expected that size matches fixture count", 2, players.size());
	}
}