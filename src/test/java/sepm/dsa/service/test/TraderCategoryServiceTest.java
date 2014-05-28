package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderCategoryService;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TraderCategoryServiceTest extends AbstractDatabaseTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private TraderCategoryService traderCategoryService;


    @Test
    public void testAdd() throws Exception {
        TraderCategory category = new TraderCategory();
        category.setName("fooTrader");
        traderCategoryService.add(category);

        TraderCategory persistedTraderCategory = traderCategoryService.get(category.getId());
        assertTrue(persistedTraderCategory != null);
    }

    @Test
    public void testRemove() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(5);
        traderCategoryService.remove(traderCategory);

        TraderCategory cat = traderCategoryService.get(5);
	    assertTrue(cat == null);
    }

    @Test(expected = DSAValidationException.class)
    public void testRemoveNotPossible() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(2);
        traderCategoryService.remove(traderCategory);
    }

    @Test
    public void testGet() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(1);
        assertNotNull(traderCategory);
    }

    @Test
    public void testGetAll() throws Exception {
        List<TraderCategory> tcs = traderCategoryService.getAll();
        assertTrue(tcs.size() > 2);
    }
}