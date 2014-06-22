package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.service.CurrencyService;
import sepm.dsa.service.CurrencySetService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CurrencySetServiceTest extends AbstractDatabaseTest {

    @Autowired
    private CurrencySetService currencySetService;

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void toCurrencySet_1() {

        CurrencySet currencySet = currencySetService.get(1);
        Integer baseRateAmount = 4321;

        Set<CurrencyAmount> expectedAmounts = new HashSet<>(4);
        CurrencyAmount a1 = new CurrencyAmount();
        a1.setCurrency(currencyService.get(3));
        a1.setAmount(1);
        CurrencyAmount a2 = new CurrencyAmount();
        a2.setCurrency(currencyService.get(4));
        a2.setAmount(2);
        CurrencyAmount a3 = new CurrencyAmount();
        a3.setCurrency(currencyService.get(5));
        a3.setAmount(3);
        CurrencyAmount a4 = new CurrencyAmount();
        a4.setCurrency(currencyService.get(6));
        a4.setAmount(4);

        expectedAmounts.add(a1);
        expectedAmounts.add(a2);
        expectedAmounts.add(a3);
        expectedAmounts.add(a4);

        Set<CurrencyAmount> amounts = new HashSet<>(currencySetService.toCurrencySet(currencySet, baseRateAmount));

        assertEquals(expectedAmounts, amounts);
    }

}
