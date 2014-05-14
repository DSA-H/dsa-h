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

    private Region region;

    @Before
    public void setup() {
        region = new Region();
        region.setName("testRegion");
        region.setColor("000000");
        region.setTemperature(Temperature.ARCTIC);
        region.setRainfallChance(RainfallChance.LOW);

        System.out.println("testSetup");
    }

    @After
    public void teardown() {
        // Teardown for data used by the unit tests
    }

    @Test
    public void testAdd() {
        int size = rs.getAll().size();
        int id = rs.add(region);

        assertTrue(rs.getAll().size() - 1 == size);
        //TODO: equals is not working right now => DONE
        assertTrue(rs.get(id).equals(region));
        assertEquals(rs.get(id), region);
        rs.remove(region);
    }

    @Test
    public void testRemove() {
        int id = rs.add(region);
        int size = rs.getAll().size();
        rs.remove(region);
        assertTrue(rs.getAll().size() + 1 == size);
    }

    @Test
    public void testUpdate() {
        int id = rs.add(region);
        int size = rs.getAll().size();
        region.setName("testRegion2");
        region.setColor("999999");
        region.setTemperature(Temperature.LOW);

        rs.update(region);
        assertTrue (rs.getAll().size() == size);
        rs.remove(region);
    }
}
