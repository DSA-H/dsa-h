package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.CurrencyDao;
import sepm.dsa.dao.UnitDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Currency;
import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;
import sepm.dsa.service.UnitTypeService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class UnitDaoTest extends AbstractDatabaseTest {

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private UnitTypeService unitTypeService;

    @Test
    public void testAdd() throws Exception {
        // TODO really test unit and not currency

        UnitType unitType = unitTypeService.get(2);

        Unit c1 = new Unit();
        c1.setName("Kilogramm");
        c1.setShortName("kg");
        c1.setUnitType(unitType);
        c1.setValueToBaseUnit(2.0);

        unitDao.add(c1);
        saveCancelService.save();

        assertNotNull(c1.getId());
    }

    @Test
    public void testRemove() throws Exception {
        unitDao.remove(unitDao.get(8));
        saveCancelService.save();
        assertNull(unitDao.get(8));
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(unitDao.getAll().size(), 8);
    }

    @Test
    public void getAllByType_returnsAllOfThisType() {

        UnitType unitType = unitTypeService.get(2);

        Unit units1 = unitDao.get(2);
        Unit units2 = unitDao.get(4);
        Set<Unit> units = new HashSet<>(2);
        units.add(units1);
        units.add(units2);

        Set<Unit> result = new HashSet<>(unitDao.getAllByType(unitType));

        assertEquals(units, result);

        //

    }

}