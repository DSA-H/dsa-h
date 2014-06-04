package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.UnitAmount;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;
import sepm.dsa.service.UnitService;
import sepm.dsa.service.UnitTypeService;

import static org.junit.Assert.*;

public class UnitServiceTest extends AbstractDatabaseTest {

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitTypeService unitTypeService;

    @Test
    public void testGet() throws Exception {
        assertNotNull(unitService.get(1));
    }

    @Test
    public void testAdd() throws Exception {
        Unit kiloGramm = new Unit();
        kiloGramm.setName("Kilogramm");
        kiloGramm.setShortName("KG");

        UnitType gewicht = new UnitType();
        gewicht.setName("Gewicht");

        kiloGramm.setUnitType(gewicht);
        kiloGramm.setValueToBaseUnit(Double.valueOf(1000));

        unitTypeService.add(gewicht);
        unitService.add(kiloGramm);
        assertNotNull(kiloGramm.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        Unit kiloGramm = new Unit();
        kiloGramm.setName("Kilogramm");
        kiloGramm.setShortName("KG");

        UnitType gewicht = new UnitType();
        gewicht.setName("Gewicht");

        kiloGramm.setUnitType(gewicht);
        kiloGramm.setValueToBaseUnit(Double.valueOf(1000));

        unitTypeService.add(gewicht);
        unitService.add(kiloGramm);

        Unit foundUnit = unitService.get(kiloGramm.getId());
        foundUnit.setName("fooasdfd");
        unitService.update(foundUnit);

        assertEquals("fooasdfd", unitService.get(kiloGramm.getId()).getName());
    }

    @Test
    public void testRemove() throws Exception {
        unitService.remove(unitService.get(1));
        assertNull(unitService.get(1));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(unitService.getAll().size(), 8);
    }

    @Test
    public void testExchange() throws Exception {

        Unit p1 = unitService.get(1);
        Unit p2 = unitService.get(2);

        Double amount = new Double(100);
        UnitAmount result = new UnitAmount();

        //exchange from p1 to p2 --> via base rate --> divide by first & multiply second
        result.setAmount(amount * p2.getValueToBaseUnit() / p1.getValueToBaseUnit());
        result.setUnit(p2);

        assertEquals(result, unitService.exchange(p1, p2, new Double(100)));
    }
}