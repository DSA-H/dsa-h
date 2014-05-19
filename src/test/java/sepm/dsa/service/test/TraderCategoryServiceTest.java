package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderCategoryService;
import sepm.dsa.service.TraderService;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class TraderCategoryServiceTest extends TestCase {

    @Autowired
    private LocationService locationService;

    @Autowired
    private TraderCategoryService traderCategoryService;


    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        TraderCategory category = new TraderCategory();
        category.setName("fooTrader");
        traderCategoryService.add(category);

        TraderCategory persistedTraderCategory = traderCategoryService.get(category.getId());
        assertTrue(persistedTraderCategory != null);
    }

    @Test(expected = DSARuntimeException.class)
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(3);
        traderCategoryService.remove(traderCategory);

        traderCategoryService.get(3);
    }

    @Test(expected = DSAValidationException.class)
    @DatabaseSetup("/testData.xml")
    public void testRemoveNotPossible() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(2);
        traderCategoryService.remove(traderCategory);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() throws Exception {
        TraderCategory traderCategory = traderCategoryService.get(1);
        assertNotNull(traderCategory);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        List<TraderCategory> tcs = traderCategoryService.getAll();
        assertTrue(tcs.size() > 2);
    }
}