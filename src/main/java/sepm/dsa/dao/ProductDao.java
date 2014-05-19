package sepm.dsa.dao;

import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductDao {
    public int add(Product product);

    public void update(Product product);

    public void remove(Product product);

    public Product get(int id);

    public List<Product> getAll();
}
