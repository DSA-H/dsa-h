package sepm.dsa.service;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.List;

public interface ProductService {
    /**
     * Get a product by its ID
     * @param id the id
     * @return the product
     */
    Product get(int id);

    /**
     * Add a new product to DB
     * @param p product (not null)
     */
    void add(Product p);

    /**
     * Update a product
     * @param p product (not null)
     */
    void update(Product p);

    /**
     * Removes a product from DB
     * @param p product (not null)
     */
    void remove(Product p);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Product> getAll();

    /**
     * @return all products from a product-category and all of its child categories -- if nothing found empty list is returned
     */
    java.util.Set<Product> getAllFromProductcategory(ProductCategory productCategory);
}
