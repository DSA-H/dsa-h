package sepm.dsa.service;

import sepm.dsa.dao.UnitAmount;
import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;

import java.util.List;

public interface UnitService {
    /**
     * Get a Unit by its ID
     *
     * @param id the id
     * @return the Unit
     */
    Unit get(Integer id);

    /**
     * Add a new Unit to DB
     *
     * @param p Unit (not null)
     */
    Unit add(Unit p);

    /**
     * Update a Unit
     *
     * @param p Unit (not null)
     */
    void update(Unit p);

    /**
     * Removes a Unit from DB
     *
     * @param p Unit (not null)
     */
    void remove(Unit p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Unit> getAll();

    /**
     * Exchanges / converts from one to the other Unit
     *
     * @param from   the original Unit must not be null
     * @param to     the foreign Unit must not be null
     * @param amount amount of from {@code Unit} to be exchanged
     * @return the value / amount of the original Unit expressed by / in the foreign Unit
     */
    UnitAmount exchange(Unit from, Unit to, Double amount);

    /**
     * @param unitType the id of the unit type
     * @return all units of a specific unit type
     */
    List<Unit> getAllByType(UnitType unitType);

}
