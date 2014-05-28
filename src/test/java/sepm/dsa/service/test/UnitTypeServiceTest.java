package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.UnitType;
import sepm.dsa.service.UnitTypeService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class UnitTypeServiceTest extends AbstractDatabaseTest {

    @Autowired
    private UnitTypeService unitTypeService;

    @Test
    public void testGet() throws Exception {
        assertNotNull(unitTypeService.get(1));
    }

    @Test
    public void testAdd() throws Exception {

        UnitType unitType = new UnitType();
        unitType.setName("foo");

        unitTypeService.add(unitType);

        assertNotNull(unitType.getId());
    }

    @Test
    public void testRemove() throws Exception {
        UnitType unitType = new UnitType();
        unitType.setName("fooRem");
        unitTypeService.add(unitType);

        unitTypeService.remove(unitType);
        assertNull(unitTypeService.get(unitType.getId()));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(unitTypeService.getAll().size(), 2);
    }

}