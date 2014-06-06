package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.CurrencyDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Currency;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class UnitDaoTest extends AbstractDatabaseTest {

    @Autowired
    private CurrencyDao currencyDao;

    @Test
    public void testAdd() throws Exception {
        // TODO really test unit and not currency
        Currency c1 = new Currency();
        c1.setName("fofods");
        c1.setValueToBaseRate(new BigDecimal(1));

        currencyDao.add(c1);

        assertNotNull(c1.getId());
    }

    @Test
    public void testRemove() throws Exception {
        currencyDao.remove(currencyDao.get(1));
        assertNull(currencyDao.get(1));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(currencyDao.getAll().size(), 2);
    }
}