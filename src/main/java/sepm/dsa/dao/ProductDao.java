package sepm.dsa.dao;

import sepm.dsa.model.Product;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductDao {
    void add(Product product);

    void update(Product product);

    void remove(Product product);

    Product get(int id);

    List<Product> getAll();
}
