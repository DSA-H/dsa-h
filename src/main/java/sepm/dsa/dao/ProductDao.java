package sepm.dsa.dao;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.List;
import java.util.Set;

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

}
