package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.ProductCategoryDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class ProductCategoryDaoTest extends AbstractDaoTest {
    @Autowired
    private ProductCategoryDao productCategoryDao;
    @Autowired
    private ProductDao productDao;

    @Test
    public void add_shouldPersistEntity() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test1");

        Set<Product> products = new HashSet<>(productDao.getAll());
        int size = products.size();
        productCategory.setProducts(products);

        productCategoryDao.add(productCategory);

        productCategory = productCategoryDao.get(productCategory.getId());
        assertNotNull(productCategory);
//        Assert.assertEquals(size, productCategory.getProducts().size());
    }

    @Test
    public void testGet() {
        ProductCategory p = productCategoryDao.get(1);
        org.junit.Assert.assertNotNull(p);
    }

    @Test
    public void add_WithParent() {
        ProductCategory parent = productCategoryDao.get(1);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test2");
        productCategory.setParent(parent);

        productCategoryDao.add(productCategory);
        productCategory = productCategoryDao.get(productCategory.getId());
        assertNotNull(productCategory);
    }

    @Test
    public void add_WithParentCycle() {
        ProductCategory parent = productCategoryDao.get(5);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test2");
        productCategory.setParent(parent);

        parent.setParent(productCategory);

        productCategoryDao.add(productCategory);
        productCategoryDao.update(parent);
        // todo: Should throw a exception because of the Cycle
    }

    @Test
    public void testRemove() {
        ProductCategory p = productCategoryDao.get(3);
        productCategoryDao.remove(p);
        assertNull(productCategoryDao.get(3));
    }

    @Test
    public void testRemoveWithChildren() {
        ProductCategory p = productCategoryDao.get(2);
        productCategoryDao.remove(p);
        assertNull(productCategoryDao.get(2));
    }
}
