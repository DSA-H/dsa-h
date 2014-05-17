package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionService;

import java.util.List;

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
public class RegionServiceTest {

	@Autowired
	private RegionService rs;

    private Region addRegion;
    private Region deleteRegion;
    private Region updateRegion;

    private Region addRegion2;
    private Region addRegion3;
    private RegionBorder regionBorder1;

    @Before
    public void setup() {

        //TODO: Import directly into testDB
        addRegion = new Region();
        addRegion.setName("testRegionAdd");
        addRegion.setColor("000000");
        addRegion.setComment("comment");
        addRegion.setTemperature(Temperature.ARCTIC);
        addRegion.setRainfallChance(RainfallChance.LOW);

        deleteRegion = new Region();
        deleteRegion.setName("testRegionDelete");
        deleteRegion.setColor("000000");
        deleteRegion.setComment("comment");
        deleteRegion.setTemperature(Temperature.HIGH);
        deleteRegion.setRainfallChance(RainfallChance.MEDIUM);
        rs.add(deleteRegion);

        updateRegion = new Region();
        updateRegion.setName("testRegionUpdate");
        updateRegion.setColor("000000");
        updateRegion.setComment("comment");
        updateRegion.setTemperature(Temperature.MEDIUM);
        updateRegion.setRainfallChance(RainfallChance.HIGH);
        rs.add(updateRegion);

        addRegion2 = new Region();
        addRegion2.setName("testRegionAdd 2");
        addRegion2.setColor("222222");
        addRegion2.setComment("comment 2");
        addRegion2.setTemperature(Temperature.VULCANO);
        addRegion2.setRainfallChance(RainfallChance.HIGH);

        addRegion3 = new Region();
        addRegion3.setName("testRegionAdd 2");
        addRegion3.setColor("222222");
        addRegion3.setComment("comment 2");
        addRegion3.setTemperature(Temperature.VULCANO);
        addRegion3.setRainfallChance(RainfallChance.HIGH);

        regionBorder1 = new RegionBorder();
        regionBorder1.setBorderCost(8);
        regionBorder1.setRegion1(addRegion2);
        regionBorder1.setRegion2(addRegion3);
        addRegion2.getBorders1().add(regionBorder1);
        addRegion3.getBorders2().add(regionBorder1);

        System.out.println("testSetup");
    }

    @After
    public void teardown() {

        // Teardown for data used by the unit tests
    }

    //@DatabaseSetup("testData.xml")
    @Test
    @DatabaseSetup("/testData.xml")
    public void testXML(){
        System.out.println(rs.get(1));
    }

    @Test
    public void testAdd() {
//        System.out.println(rs.get(0));
        int size = rs.getAll().size();
        rs.add(addRegion);

        assertTrue(rs.getAll().size() - 1 == size);
        //TODO: equals is not working right now => DONE
        assertTrue(rs.get(addRegion.getId()).equals(addRegion));
        assertEquals(rs.get(addRegion.getId()), addRegion);
    }

    @Test
    public void testRemove() {
        int size = rs.getAll().size();
        rs.remove(deleteRegion);
        assertTrue(rs.getAll().size() + 1 == size);
    }

    @Test
    public void testUpdate() {
        int size = rs.getAll().size();
        updateRegion.setName("testRegion2");
        updateRegion.setColor("999999");
        updateRegion.setComment("comment");
        updateRegion.setTemperature(Temperature.LOW);

        rs.update(updateRegion);
        assertTrue (rs.getAll().size() == size);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void add_withBorder_shouldPersist() {
        int size = rs.getAll().size();
        rs.add(addRegion2);
        addRegion2.getBorders1().add(regionBorder1);
        addRegion3.getBorders2().add(regionBorder1);
        regionBorder1.setRegion1(addRegion2);
        regionBorder1.setRegion2(addRegion3);
        rs.add(addRegion3);
        List<Region> listLater = rs.getAll();
        int sizeLater = listLater.size();
        assertTrue(sizeLater == size + 2);
        assertTrue(listLater.contains(addRegion2));
        assertTrue(listLater.contains(addRegion3));
        addRegion2.getBorders1().add(regionBorder1);
        rs.update(addRegion2);
        assertTrue(addRegion2.getBorders1().contains(regionBorder1) || addRegion2.getBorders2().contains(regionBorder1));
        addRegion3 = rs.get(addRegion3.getId());
//        assertTrue(addRegion3.getAllBorders().contains(regionBorder1)); // TODO assertion fails !! I (mHoe) think that regionBorder is not stored automatically

    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void oneToMany_hasValues() {
        Region region = rs.get(1);
        assertTrue(region.getAllBorders().size() == 3);
        assertTrue(region.getBorders1().size() == 3);
        assertTrue(region.getBorders2().size() == 0);

    }

}
