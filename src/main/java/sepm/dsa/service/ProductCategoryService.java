package sepm.dsa.service;

import sepm.dsa.model.ProductCategory;

import java.util.List;

public interface ProductCategoryService {
    /**
     * Get a productcategory by its ID
     *
     * @param id the id
     * @return the productcategory
     */
    ProductCategory get(Integer id);

    /**
     * Add a new productcategory to DB
     *
     * @param p productcategory (not null)
     * @return The added productCategory model.
     */
    ProductCategory add(ProductCategory p);

    /**
     * Update a product
     *
     * @param p product (not null)
     * @return The updated productCategory model.
     */
    ProductCategory update(ProductCategory p);

    /**
     * Removes a productcategory from DB
     *
     * @param p productcategory (not null)
     */
    void remove(ProductCategory p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<ProductCategory> getAll();
}
