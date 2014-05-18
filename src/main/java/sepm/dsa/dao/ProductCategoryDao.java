package sepm.dsa.dao;

import sepm.dsa.model.ProductCategory;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductCategoryDao {
    public int add(ProductCategory productCategory);

    public void update(ProductCategory productCategory);

    public void remove(ProductCategory productCategory);

    public ProductCategory get(Integer id);

    public List<ProductCategory> getAll();
}
