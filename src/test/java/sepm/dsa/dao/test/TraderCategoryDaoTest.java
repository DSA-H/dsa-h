package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.TraderCategory;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TraderCategoryDaoTest extends AbstractDatabaseTest {

    @Autowired
    private TraderCategoryDao traderCategoryDao;

    @Test
    public void testAdd() throws Exception {
        TraderCategory myCategory = new TraderCategory();
        myCategory.setName("fooTrader");
        traderCategoryDao.add(myCategory);

        TraderCategory persTraderCat = traderCategoryDao.get(myCategory.getId());
        assertTrue(persTraderCat != null);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testAdd_null_shouldFail() throws Exception {
        traderCategoryDao.add(null);
    }

    @Test
    public void testRemove() throws Exception {
        TraderCategory myCategory = new TraderCategory();
        myCategory.setName("fooTrader");
        traderCategoryDao.add(myCategory);
        traderCategoryDao.remove(myCategory);
        TraderCategory cat = traderCategoryDao.get(myCategory.getId());
	    assertTrue(cat == null);
    }

    @Test
    public void testGetAll() throws Exception {
        TraderCategory cat1 = traderCategoryDao.get(1);
        List<TraderCategory> l1 = traderCategoryDao.getAll();
        assertTrue(l1.contains(cat1));
    }
}