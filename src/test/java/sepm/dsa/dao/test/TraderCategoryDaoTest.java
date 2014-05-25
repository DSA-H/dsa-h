package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.ProductCategoryService;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TraderCategoryDaoTest extends AbstractDatabaseTest {

    @Autowired
    private TraderCategoryDao traderCategoryDao;

    @Autowired
    private ProductCategoryService productCategoryService;

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

    @Test
    public void add_withAssortmentNatures_ShouldCascadeAddAssortments() {
        TraderCategory cat = new TraderCategory();
        cat.setName("Kat 1");
        AssortmentNature a1 = new AssortmentNature();
        a1.setDefaultOccurence(50);
        ProductCategory pc = productCategoryService.get(1);
        a1.setProductCategory(pc);
        a1.setTraderCategory(cat);
        cat.getAssortments().put(pc, a1);
        cat = traderCategoryDao.get(cat.getId());
        assertNotNull(cat);
        assertTrue("assortments didn't cascade persist", cat.getAssortments().size() == 1);
    }

}