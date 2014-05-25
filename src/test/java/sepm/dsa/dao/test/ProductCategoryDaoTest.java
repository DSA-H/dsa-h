package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.ProductCategoryDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class ProductCategoryDaoTest extends AbstractDatabaseTest {
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

    @Test
    public void getAll_getsAll() {
        ProductCategory c1 = productCategoryDao.get(1);
        ProductCategory c2 = productCategoryDao.get(2);
        ProductCategory c3 = productCategoryDao.get(3);
        ProductCategory c4 = productCategoryDao.get(4);
        ProductCategory c5 = productCategoryDao.get(5);

        List<ProductCategory> all = productCategoryDao.getAll();
        assertThat(all, hasItems(c1, c2, c3, c4, c5));
        assertEquals(5, all.size());
    }
}
