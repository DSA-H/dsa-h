package sepm.dsa.dao.test;

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
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.model.TraderCategory;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class TraderCategoryDaoTest extends TestCase {

    @Autowired
    private TraderCategoryDao traderCategoryDao;

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        TraderCategory myCategory = new TraderCategory();
        myCategory.setName("fooTrader");
        //TODO change
        myCategory.setAssortments(null);

        TraderCategory persTraderCat = traderCategoryDao.get(myCategory.getId());
        assertTrue(persTraderCat != null);
    }

    @Test
    public void testAdd_null_shouldFail() throws Exception {
        traderCategoryDao.add(null);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        TraderCategory traderCategory = traderCategoryDao.get(1);
        traderCategoryDao.remove(traderCategory);

        assertEquals(null, traderCategoryDao.get(traderCategory.getId()));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAllByTraderCategory() throws Exception {
        TraderCategory cat1 = traderCategoryDao.get(1);
        TraderCategory l1 = traderCategoryDao.get(1);
        TraderCategory l2 = traderCategoryDao.get(2);
        assertThat(traderCategoryDao.getAllByTraderCategory(cat1), hasItems(l1, l2));
    }
}