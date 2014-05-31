package sepm.dsa.service.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Player;
import sepm.dsa.service.PlayerService;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Transactional
public class PlayerServiceTest extends AbstractDatabaseTest {

    @Autowired
    private PlayerService ps;

    @Test
    public void testAdd() throws Exception {
        Player player = new Player();
        player.setName("testPlayer");
        player.setComment("comment!!!");

        int size = ps.getAll().size();
        ps.add(player);
        saveCancelService.save();

        assertTrue(size+1==ps.getAll().size(), "Größe hat sich nach dem hinzufügen nicht um eins erhöht");
        assertTrue(ps.get(player.getId()).equals(player), "Der hinzugefügte Spieler gleicht dem Spieler der entsprechenden Id nicht!");
    }

    @Test
    public void testUpdate() throws Exception {
        int size = ps.getAll().size();
        Player player = ps.get(1);
        player.setName("neuerName");
        player.setComment("anderesComment");
        ps.update(player);
        saveCancelService.save();

        assertTrue(size==ps.getAll().size(), "Größe hat sich nach dem update verändert");
        assertTrue(ps.get(player.getId()).equals(player), "Der veränderte Spieler gleicht dem Spieler der entsprechenden Id nicht!");
    }

    @Test
    public void testRemove() throws Exception {
        Player player = ps.get(1);

        int size = ps.getAll().size();
        ps.remove(player);
        saveCancelService.save();

        assertEquals(size - 1, ps.getAll().size());
        assertNull(ps.get(1));
    }

    @Test
    public void testGet() throws Exception {
        //<players id="2" name="Mr Z" comment="A test comment"/>
        Player player = ps.get(2);
        assertTrue(player.getName().equals("Mr Z"), "Name passt nicht überein");
        assertTrue(player.getComment().equals("A test comment"), "Kommentar passt nicht überein");

    }

    @Test
    public void testGetAll() throws Exception {
        assertTrue(ps.getAll().size()==2, "Größe der Player passt nicht überein");

    }
}