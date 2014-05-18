package sepm.dsa.service;

import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.ProductUnit;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductUnitService {
    /**
     * Get a productcategory by its ID
     * @param id the id
     * @return the productcategory
     */
    ProductUnit get(Integer id);

    /**
     * Add a new productcategory to DB
     * @param p productcategory (not null)
     */
    int add(ProductUnit p);

    /**
     * Update a product
     * @param p product (not null)
     */
    void update(ProductUnit p);

    /**
     * Removes a productcategory from DB
     * @param p productcategory (not null)
     */
    void remove(ProductUnit p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<ProductUnit> getAll();
}
