package sepm.dsa.service;

import sepm.dsa.dao.UnitAmount;
import sepm.dsa.model.Unit;

import java.util.List;

public interface UnitService {
    /**
     * Get a productcategory by its ID
     *
     * @param id the id
     * @return the productcategory
     */
    Unit get(Integer id);

    /**
     * Add a new productcategory to DB
     *
     * @param p productcategory (not null)
     */
    int add(Unit p);

    /**
     * Update a product
     *
     * @param p product (not null)
     */
    void update(Unit p);

    /**
     * Removes a productcategory from DB
     *
     * @param p productcategory (not null)
     */
    void remove(Unit p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Unit> getAll();

    /**
     * Exchanges / converts from one to the other ProductUnit
     *
     * @param from   the original Currency must not be null
     * @param to     the foreign ProductUnit must not be null
     * @param amount amount of from {@code Currency} to be exchanged
     * @return the value / amount of the original ProductUnit expressed by / in the foreign ProductUnit
     */
    UnitAmount exchange(Unit from, Unit to, Double amount);
}
