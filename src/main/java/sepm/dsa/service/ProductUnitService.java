package sepm.dsa.service;

import sepm.dsa.model.ProductUnit;

import java.math.BigDecimal;
import java.util.List;

public interface ProductUnitService {
    /**
     * Get a productcategory by its ID
     *
     * @param id the id
     * @return the productcategory
     */
    ProductUnit get(Integer id);

    /**
     * Add a new productcategory to DB
     *
     * @param p productcategory (not null)
     */
    int add(ProductUnit p);

    /**
     * Update a product
     *
     * @param p product (not null)
     */
    void update(ProductUnit p);

    /**
     * Removes a productcategory from DB
     *
     * @param p productcategory (not null)
     */
    void remove(ProductUnit p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<ProductUnit> getAll();

    /**
     * Exchanges / converts from one to the other ProductUnit
     *
     * @param from   the original Currency must not be null
     * @param to     the foreign ProductUnit must not be null
     * @param amount amount of from {@code Currency} to be exchanged
     * @return the value / amount of the original ProductUnit expressed by / in the foreign ProductUnit
     */
    ProductUnit exchange(ProductUnit from, ProductUnit to, BigDecimal amount);
}
