package sepm.dsa.dao.test;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.CurrencyDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.service.CurrencySetService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class CurrencyDaoTest extends AbstractDatabaseTest {

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private CurrencySetService currencySetService;


    @Test
    public void testAdd() throws Exception {
        Currency c1 = new Currency();
        c1.setName("fofods");
        c1.setValueToBaseRate(1);

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
        assertEquals(6, currencyDao.getAll().size());
    }

    @Test
    public void getAllByCurrencySet_returnsAllInSetOrderByMostValuable() {

        CurrencySet currencySet = currencySetService.get(1);
        List<Currency> currencies = currencyDao.getAllByCurrencySet(currencySet);

        Currency last = currencies.get(0);
        for (int i=1; i<currencies.size(); i++) {
            assertTrue("The order is not descending the valueToBaseRate",
                    last.getValueToBaseRate().compareTo(currencies.get(i).getValueToBaseRate()) == 1);  //this greater than that
        }

    }

    @Test
    public void getAllByCurrencySet_containsEntries() {

        Currency c1 = currencyDao.get(2);
        Currency c2 = currencyDao.get(3);
        Set<Currency> expectedCurrencies = new HashSet<>(2);
        expectedCurrencies.add(c1);
        expectedCurrencies.add(c2);

        CurrencySet currencySet = currencySetService.get(2);
        Set<Currency> currencies = new HashSet<>(currencyDao.getAllByCurrencySet(currencySet));

        Assert.assertEquals(expectedCurrencies, currencies);

    }


}