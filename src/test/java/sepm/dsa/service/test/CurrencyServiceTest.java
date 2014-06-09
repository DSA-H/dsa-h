package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.service.CurrencyService;
import sepm.dsa.service.CurrencySetService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class CurrencyServiceTest extends AbstractDatabaseTest {

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void testGet() throws Exception {
        assertNotNull(currencyService.get(1));
    }

    @Test
    public void testAdd() throws Exception {
        Currency c1 = new Currency();
        c1.setName("fofods");
        c1.setValueToBaseRate(3);

        currencyService.add(c1);

        assertNotNull(c1.getId());
    }

    @Test
    public void testRemove() throws Exception {
        currencyService.remove(currencyService.get(1));
        assertNull(currencyService.get(1));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(6, currencyService.getAll().size());
    }

    @Test
    public void testExchange_roundUp() throws Exception {

        Currency c1 = currencyService.get(1); //to base rate: 3
        Currency c2 = currencyService.get(2);// to base rate: 2
        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second

        Integer amountToExchange = 100;
        Integer referenceCalculation = (int) ((((double) amountToExchange) * c2.getValueToBaseRate()) / c1.getValueToBaseRate() + 0.5);//, 4, RoundingMode.HALF_UP);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(referenceCalculation);
        currencyAmount.setCurrency(c2);
        assertEquals(currencyAmount, currencyService.exchange(c1, c2, 100));
    }

    @Test
    public void testExchange_roundDown() throws Exception {

        Currency c1 = currencyService.get(1); //to base rate: 3
        Currency c2 = currencyService.get(2);// to base rate: 2
        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second

        Integer amountToExchange = 50;
        Integer referenceCalculation = (int) ((((double) amountToExchange) * c2.getValueToBaseRate()) / c1.getValueToBaseRate() + 0.5);//, 4, RoundingMode.HALF_UP);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(referenceCalculation);
        currencyAmount.setCurrency(c2);
        assertEquals(currencyAmount, currencyService.exchange(c1, c2, 50));
    }


}