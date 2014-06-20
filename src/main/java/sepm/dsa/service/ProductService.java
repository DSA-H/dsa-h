package sepm.dsa.service;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;

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
     * Search for all Products containing the searchTerm in Product.name and all
     * Products with Product(super)Category containing the name
     * @param searchTerm must not be null
     * @return set of products -- or empty set if nothing found (not null)
     */
    Set<Product> getBySearchTerm(String searchTerm);

    /**
     * @param region the region, not null
     * @return all products produced in this region, might be an empty list (not null)
     */
    List<Product> getAllByProductionRegion(Region region);

    /**
     * @param productOrCategoryName use null for not filter this parameter
     * @param regionName use null for not filter this parameter
     * @return all Products metting following conditions: <br />
     *          (Product.name like :productOrCategoryName or :productOrCategoryName in Product.categories.name)
     *          and (:regionName in Product.productionRegions.name) <br />
     *          might be an empty list (not null)
     *
     */
    List<Product> getAllByFilter(String productOrCategoryName, String regionName);

}
