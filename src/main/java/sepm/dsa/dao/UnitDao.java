package sepm.dsa.dao;

import sepm.dsa.model.Unit;

import java.util.List;

public interface UnitDao {
    /**
     * Persists a {@code Unit} in the Database
     *
     * @param unit to be persisted must not be null
     * @return
     */
    public int add(Unit unit);

    /**
     * Updates a already existing {@code Unit} in the database
     *
     * @param unit to update must not be null
     */
    public void update(Unit unit);

    /**
     * Delete a unit permanently
     *
     * @param unit to be deleted must not be null
     */
    public void remove(Unit unit);

    /**
     * Finds a {@code Unit} by its ID
     *
     * @param id the primay key
     * @return the unit, or null, if no such unit exists
     */
    public Unit get(Integer id);

    /**
     * Finds all {@code Unit}s in te DB
     *
     * @return the units or empty list if no units exist (not null)
     */
    public List<Unit> getAll();
}
