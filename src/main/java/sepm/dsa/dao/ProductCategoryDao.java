package sepm.dsa.dao;

import sepm.dsa.model.ProductCategory;

import java.util.List;

public interface ProductCategoryDao extends BaseDao<ProductCategory> {

    /**
     *
     * @param parent parent ProductCategory, use null for 'no parent'
     * @return all {@code ProductCategory}s with this parent
     */
    List<ProductCategory> getAllByParent(ProductCategory parent);

    /**
     * @param name
     * @return all ProductCategory by name, may be an empty list (not null)
     */
    List<ProductCategory> getAllByName(String name);

}
