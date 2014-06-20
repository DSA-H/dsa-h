package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;

import java.util.ArrayList;
import java.util.List;

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
        c1.setShortName("i31");
        c1.setValueToBaseRate(3);

        currencyService.add(c1);

        assertNotNull(c1.getId());
    }

    @Test
    public void testRemove() throws Exception {
        currencyService.remove(currencyService.get(5));
        assertNull(currencyService.get(5));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(6, currencyService.getAll().size());
    }

    @Test
    public void testExchange_roundUp() throws Exception {

        Currency from = currencyService.get(1); //to base rate: 3
        Currency to = currencyService.get(2);// to base rate: 2
        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second

        Integer amountToExchange = 100;
        Integer referenceCalculation = (int) ((((double) amountToExchange) * from.getValueToBaseRate()) / to.getValueToBaseRate() + 0.5);//, 4, RoundingMode.HALF_UP);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(referenceCalculation);
        currencyAmount.setCurrency(to);
        assertEquals(currencyAmount, currencyService.exchange(from, to, 100));
    }

    @Test
    public void testExchange_roundDown() throws Exception {

        Currency from = currencyService.get(1); //to base rate: 3
        Currency to = currencyService.get(2);// to base rate: 2
        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second

        Integer amountToExchange = 50;
        Integer referenceCalculation = (int) ((((double) amountToExchange) * from.getValueToBaseRate()) / to.getValueToBaseRate() + 0.5);//, 4, RoundingMode.HALF_UP);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(referenceCalculation);
        currencyAmount.setCurrency(to);
        assertEquals(currencyAmount, currencyService.exchange(from, to, 50));
    }


    @Test
    public void exchangeToBaseRate_correctValue() {

        Currency c1 = currencyService.get(3);
        Currency c2 = currencyService.get(4);

        List<CurrencyAmount> currencyAmounts = new ArrayList<>();
        CurrencyAmount ca1 = new CurrencyAmount();
        ca1.setAmount(2);
        ca1.setCurrency(c1);

        CurrencyAmount ca2 = new CurrencyAmount();
        ca2.setAmount(4);
        ca2.setCurrency(c2);

        currencyAmounts.add(ca1);
        currencyAmounts.add(ca2);
        Integer baseRatePrice = currencyService.exchangeToBaseRate(currencyAmounts);

        assertEquals(new Integer(42), baseRatePrice);

    }


}