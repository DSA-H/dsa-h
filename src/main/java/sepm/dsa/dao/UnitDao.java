package sepm.dsa.dao;

import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;

import java.util.List;

public interface UnitDao extends BaseDao<Unit> {

    /**
     * @param unitType the unit type
     * @return all units of a specific unit type, might be an empty list (not null)
     */
    List<Unit> getAllByType(UnitType unitType);

}
