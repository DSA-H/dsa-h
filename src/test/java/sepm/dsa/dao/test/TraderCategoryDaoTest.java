package sepm.dsa.dao.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.TraderCategory;

import java.util.List;

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
public class TraderCategoryDaoTest {

    @Autowired
    private TraderCategoryDao traderCategoryDao;

    @Test
    @DatabaseSetup("/testData.xml")
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

    @Test(expected = sepm.dsa.exceptions.DSARuntimeException.class)
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        TraderCategory myCategory = new TraderCategory();
        myCategory.setName("fooTrader");
        traderCategoryDao.add(myCategory);
        traderCategoryDao.remove(myCategory);
        traderCategoryDao.get(myCategory.getId());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        TraderCategory cat1 = traderCategoryDao.get(1);
        List<TraderCategory> l1 = traderCategoryDao.getAll();
        assertTrue(l1.contains(cat1));
    }
}