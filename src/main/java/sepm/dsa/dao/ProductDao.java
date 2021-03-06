package sepm.dsa.dao;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;

import java.util.List;

public interface ProductDao extends BaseDao<Product> {

    /**
     *
     * @param productCategory not null
     * @return all products from a product-category and all of its child categories -- if nothing found empty list is returned
     */
    List<Product> getAllByCategoryPlusChildren(ProductCategory productCategory);

    /**
     * Get all Products containg 'name' in its name
     *
     * @param name the search string, not null
     * @return all products containing 'name'-parameter value in its name, might be empty (not null)
     */
    List<Product> getAllByName(String name);

    /**
     * @param region the region, not null
     * @return all products produced in this region, might be an empty list (not null)
     */
    List<Product> getAllByRegion(Region region);

    /**
     * @param regionName the regionName, not null
     * @return all products produced in this region, might be an empty list (not null)
     */
    List<Product> getAllByRegionName(String regionName);

}
