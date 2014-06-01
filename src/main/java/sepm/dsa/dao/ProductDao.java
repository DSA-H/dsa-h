package sepm.dsa.dao;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.List;

public interface ProductDao extends BaseDao<Product> {

    /**
     *
     * @param productCategory not null
     * @return all products from a product-category and all of its child categories -- if nothing found empty list is returned
     */
    List<Product> getAllByCategoryPlusChildren(ProductCategory productCategory);

}
