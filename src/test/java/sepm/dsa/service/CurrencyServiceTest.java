package sepm.dsa.service;

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
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class CurrencyServiceTest extends TestCase {

    @Autowired
    private CurrencyService currencyService;

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() throws Exception {
        assertNotNull(currencyService.get(1));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        Currency c1 = new Currency();
        c1.setName("fofods");
        c1.setValueToBaseRate(new BigDecimal(3));

        currencyService.add(c1);

        assertNotNull(c1.getId());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        currencyService.remove(currencyService.get(1));
        assertNull(currencyService.get(1));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        assertEquals(currencyService.getAll().size(), 2);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testExchange() throws Exception {

        Currency c1 = currencyService.get(1); //to base rate: 3
        Currency c2 = currencyService.get(2);// to base rate: 2
        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second

        BigDecimal amountToExchange = new BigDecimal(100);
        BigDecimal referenceCalculation = amountToExchange.multiply(c2.getValueToBaseRate()).divide(c1.getValueToBaseRate(), 4, RoundingMode.HALF_UP);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(referenceCalculation);
        currencyAmount.setCurrency(c2);
        assertEquals(currencyAmount, currencyService.exchange(c1, c2, new BigDecimal(100)));
    }
}