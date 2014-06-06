package sepm.dsa.service;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.List;
import java.util.Set;

public interface ProductService {
    /**
     * Get a product by its ID
     *
     * @param id the id
     * @return the product
     */
    Product get(int id);

    /**
     * Add a new product to DB
     *
     * @param p product (not null)
     * @return The added category model.
     */
    Product add(Product p);

    /**
     * Update a product
     *
     * @param p product (not null)
     * @return The updated category model.
     */
    Product update(Product p);

    /**
     * Removes a product from DB
     *
     * @param p product (not null)
     */
    void remove(Product p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Product> getAll();

    /**
     * @param productCategory not null
     * @return all products from a product-category and all of its child categories -- if nothing found empty list is returned
     */
    Set<Product> getAllFromProductcategory(ProductCategory productCategory);

    /**
     * Search for all Products by name (incremental search)
     * @param searchTerm must not be null
     * @return set of products -- or empty set if nothing found
     */
    Set<Product> getBySearchTerm(String searchTerm);
}
