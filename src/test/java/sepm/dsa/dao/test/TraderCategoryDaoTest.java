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
        myCategory.setAssortments();

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

    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAllByTraderCategory() throws Exception {

    }
}