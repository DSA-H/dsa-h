package sepm.dsa.dao;

import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;

import java.util.List;

public interface UnitDao extends BaseDao<Unit> {

    /**
     * @param unitTypeID the id of the unit type
     * @return all units of a specific unit type
     */
    List<Unit> getAllByType(UnitType unitType);

}
