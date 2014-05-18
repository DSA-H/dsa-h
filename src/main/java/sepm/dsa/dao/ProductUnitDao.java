package sepm.dsa.dao;

import sepm.dsa.model.ProductUnit;

import java.util.List;

/**
 * Created by Chris on 17.05.2014.
 */
public interface ProductUnitDao {
    public int add(ProductUnit productUnit);

    public void update(ProductUnit productUnit);

    public void remove(ProductUnit productUnit);

    public ProductUnit get(Integer id);

    public List<ProductUnit> getAll();
}
