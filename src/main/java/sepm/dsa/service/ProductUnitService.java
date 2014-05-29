package sepm.dsa.service;

import sepm.dsa.model.ProductUnit;

import java.util.List;

public interface ProductUnitService {
    /**
     * Get a productUnit by its ID
     *
     * @param id the id
     * @return the productUnit
     */
    ProductUnit get(Integer id);

    /**
     * Add a new productUnit to DB
     *
     * @param p productUnit (not null)
     * @return The added productUnit model.
     */
    ProductUnit add(ProductUnit p);

    /**
     * Update a product
     *
     * @param p product (not null)
     * @return The updated productUnit model.
     */
    ProductUnit update(ProductUnit p);

    /**
     * Removes a productUnit from DB
     *
     * @param p productUnit (not null)
     */
    void remove(ProductUnit p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<ProductUnit> getAll();
}
