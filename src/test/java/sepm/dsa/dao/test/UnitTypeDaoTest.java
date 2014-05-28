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
        unitTypeDao.remove(unitTypeDao.get(1));
        assertNull(unitTypeDao.get(1));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(2, unitTypeDao.getAll().size());
    }
}