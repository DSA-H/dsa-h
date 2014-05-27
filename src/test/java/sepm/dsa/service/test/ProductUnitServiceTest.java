package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.ProductUnit;
import sepm.dsa.service.ProductUnitService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class ProductUnitServiceTest extends AbstractDatabaseTest {

    @Autowired
    private ProductUnitService productUnitService;

    @Test
    public void testGet() throws Exception {
        assertNotNull(productUnitService.get(1));
    }

    @Test
    public void testAdd() throws Exception {
        ProductUnit p1 = new ProductUnit();
        p1.setName("foo 1");
        p1.setValue(BigDecimal.valueOf(100));
        p1.setUnitType("type45435");
        productUnitService.add(p1);
        assertNotNull(p1.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        ProductUnit p1 = new ProductUnit();
        p1.setName("foo 1");
        p1.setValue(BigDecimal.valueOf(100));
        p1.setUnitType("type45435");
        productUnitService.add(p1);

        ProductUnit foundUnit = productUnitService.get(p1.getId());
        foundUnit.setName("fooasdfd");
        productUnitService.update(foundUnit);

        assertEquals("fooasdfd", productUnitService.get(p1.getId()).getName());
    }

    @Test
    public void testRemove() throws Exception {
        productUnitService.remove(productUnitService.get(1));
        assertNull(productUnitService.get(1));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(productUnitService.getAll().size(), 2);
    }

    @Test
    public void testExchange() throws Exception {

        ProductUnit p1 = productUnitService.get(1);
        ProductUnit p2 = productUnitService.get(2);

        BigDecimal amount = new BigDecimal(100);
        ProductUnit result = new ProductUnit();

        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second
        result.setValue(amount.multiply(p2.getValue()).divide(p1.getValue(), 4, RoundingMode.HALF_UP));
        result.setUnitType(p2.getUnitType());

        assertEquals(result, productUnitService.exchange(p1, p2, new BigDecimal(100)));
    }
}