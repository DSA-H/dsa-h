package sepm.dsa.dao;

import sepm.dsa.model.Product;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductDao {
    public void add(Product product);

    public void update(Product product);

    public void remove(Product product);

    public Product get(String name);

    public List<Product> getAll();
}
