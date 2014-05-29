package sepm.dsa.dao;

import sepm.dsa.model.UnitType;

import java.util.List;

public interface UnitTypeDao {

    /**
     * Persists a {@code sepm.dsa.model.UnitType} in the Database
     *
     * @param unitType to be persisted must not be null
     * @return
     */
    public int add(UnitType unitType);

    /**
     * Updates a already existing {@code sepm.dsa.model.UnitType} in the database
     *
     * @param unitType to update must not be null
     */
    public void update(UnitType unitType);

    /**
     * Delete a unitType permanently
     *
     * @param unitType to be deleted must not be null
     */
    public void remove(UnitType unitType);

    /**
     * Finds a {@code sepm.dsa.model.UnitType} by its ID
     *
     * @param id the primay key
     * @return the unitType, or null, if no such unitType exists
     */
    public UnitType get(Integer id);

    /**
     * Finds all {@code UnitType}s in te DB
     *
     * @return the unitTypes or empty list if no unitTypes exist (not null)
     */
    public List<UnitType> getAll();
}
