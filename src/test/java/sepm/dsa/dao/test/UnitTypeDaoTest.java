package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.UnitTypeDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.UnitType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class UnitTypeDaoTest extends AbstractDatabaseTest {

    @Autowired
    private UnitTypeDao unitTypeDao;

    @Test
    public void testAdd() throws Exception {
        UnitType u1 = new UnitType();
        u1.setName("fofods");

        unitTypeDao.add(u1);
        assertNotNull(u1.getId());
    }

    @Test
    public void testRemove() throws Exception {
        UnitType unitType = new UnitType();
        unitType.setName("fooRem");
        unitTypeDao.add(unitType);

        unitTypeDao.remove(unitType);
        assertNull(unitTypeDao.get(unitType.getId()));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(2, unitTypeDao.getAll().size());
    }
}