package sepm.dsa.service.test;

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
import sepm.dsa.model.Player;
import sepm.dsa.service.PlayerService;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class PlayerServiceTest {
    @Autowired
    private PlayerService ps;

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        Player player = new Player();
        player.setName("testPlayer");
        player.setComment("comment!!!");

        int size = ps.getAll().size();
        ps.add(player);
        assertTrue(size+1==ps.getAll().size(), "Größe hat sich nach dem hinzufügen nicht um eins erhöht");
        assertTrue(ps.get(player.getId()).equals(player), "Der hinzugefügte Spieler gleicht dem Spieler der entsprechenden Id nicht!");
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testUpdate() throws Exception {
        int size = ps.getAll().size();
        Player player = ps.get(1);
        player.setName("neuerName");
        player.setComment("anderesComment");
        ps.update(player);
        assertTrue(size==ps.getAll().size(), "Größe hat sich nach dem update verändert");
        assertTrue(ps.get(player.getId()).equals(player), "Der veränderte Spieler gleicht dem Spieler der entsprechenden Id nicht!");
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        Player player = ps.get(1);

        int size = ps.getAll().size();
        ps.remove(player);
        assertTrue(size-1==ps.getAll().size(), "Größe hat sich nach dem löschen nicht um eins verringert");
        assertTrue(ps.get(1)==null, "Der Spieler existiert trotz entfernen noch!");
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() throws Exception {
        //<players id="2" name="Mr Z" comment="A test comment"/>
        Player player = ps.get(2);
        System.out.println(player.getName());
        assertTrue(player.getName().equals("Mr Z"), "Name passt nicht überein");
        assertTrue(player.getComment().equals("A test comment"), "Kommentar passt nicht überein");

    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        assertTrue(ps.getAll().size()==2, "Größe der Player passt nicht überein");

    }
}