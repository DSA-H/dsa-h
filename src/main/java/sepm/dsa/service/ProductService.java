package sepm.dsa.service;

import sepm.dsa.model.Product;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductService {
    /**
     * Get a product by its ID
     * @param id the id
     * @return the product
     */
    Product get(Integer id);

    /**
     * Add a new product to DB
     * @param p product (not null)
     */
    int add(Product p);

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
}
