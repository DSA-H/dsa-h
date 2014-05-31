package sepm.dsa.service;

import sepm.dsa.model.UnitType;

import java.util.List;

public interface UnitTypeService {

    /**
     * Get a {@code UnitType} by its ID
     *
     * @param id the id
     * @return the {@code UnitType}
     */
    UnitType get(Integer id);

    /**
     * Add a new {@code UnitType} to DB
     *
     * @param p {@code UnitType} (not null)
     */
    int add(UnitType p);

    /**
     * Update a UnitType
     *
     * @param p UnitType (not null)
     */
    void update(UnitType p);

    /**
     * Removes a {@code UnitType} from DB
     *
     * @param p {@code UnitType} (not null)
     */
    void remove(UnitType p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<UnitType> getAll();

}
