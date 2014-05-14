package sepm.dsa.service.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class RegionServiceTest {

	@Autowired
	private RegionService rs;

    private Region addRegion;
    private Region deleteRegion;
    private Region updateRegion;

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

        System.out.println("testSetup");
    }

    @After
    public void teardown() {

        // Teardown for data used by the unit tests
    }

    @Test
    public void testAdd() {
        int size = rs.getAll().size();
        int id = rs.add(addRegion);

        assertTrue(rs.getAll().size() - 1 == size);
        //TODO: equals is not working right now => DONE
        assertTrue(rs.get(id).equals(addRegion));
        assertEquals(rs.get(id), addRegion);
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
}
