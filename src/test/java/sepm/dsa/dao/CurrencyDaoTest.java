package sepm.dsa.dao;

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
import sepm.dsa.model.Currency;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class CurrencyDaoTest extends TestCase {

    @Autowired
    private CurrencyDao currencyDao;

    @Test
    public void testAdd() throws Exception {
        Currency c1 = new Currency();
        c1.setName("fofods");
        c1.setValueToBaseRate(1);

        currencyDao.add(c1);

        assertNotNull(c1.getId());
    }

    @Test
    @DatabaseSetup("/testCurrency.xml")
    public void testRemove() throws Exception {
        currencyDao.remove(currencyDao.get(1));
        assertNull(currencyDao.get(1));
    }

    @Test
    @DatabaseSetup("/testCurrency.xml")
    public void testGetAll() throws Exception {
        assertEquals(currencyDao.getAll().size(), 2);
    }
}