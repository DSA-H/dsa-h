package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.UnitAmount;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;
import sepm.dsa.service.UnitService;

import static org.junit.Assert.*;

public class UnitServiceTest extends AbstractDatabaseTest {

    @Autowired
    private UnitService productUnitService;

    @Test
    public void testGet() throws Exception {
        assertNotNull(productUnitService.get(1));
    }

    @Test
    public void testAdd() throws Exception {
        Unit kiloGramm = new Unit();
        kiloGramm.setName("Kilogramm");
        kiloGramm.setShortName("KG");
        UnitType gewicht = new UnitType();
        gewicht.setName("Gewicht");
        gewicht.setBaseUnit(kiloGramm);

        kiloGramm.setUnitType(gewicht);
        kiloGramm.setValueToBaseUnit(Double.valueOf(1000));

        productUnitService.add(kiloGramm);
        assertNotNull(kiloGramm.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        Unit p1 = new Unit();
        p1.setName("foo 1");
        p1.setValue(Double.valueOf(100));
        p1.setUnitType("type45435");
        productUnitService.add(p1);

        Unit foundUnit = productUnitService.get(p1.getId());
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

        Unit p1 = productUnitService.get(1);
        Unit p2 = productUnitService.get(2);

        Double amount = new Double(100);
        UnitAmount result = new UnitAmount();

        //exchange from c1 to c2 --> via base rate --> divide by first & multiply second
        result.setAmount(amount * p2.getValue() / p1.getValue());
        result.setUnit(p2);

        assertEquals(result, productUnitService.exchange(p1, p2, new Double(100)));
    }
}