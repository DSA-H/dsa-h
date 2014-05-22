package sepm.dsa.dao.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.dao.ProductCategoryDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductAttribute;
import sepm.dsa.model.ProductCategory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jotschi on 22.05.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class ProductCategoryDaoTest {
    @Autowired
    private ProductCategoryDao productCategoryDao;
    @Autowired
    private ProductDao productDao;

    @Test
    @DatabaseSetup("/testData.xml")
    public void add_shouldPersistEntity() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test1");

        Set<Product> products = new HashSet<>(productDao.getAll());
        int size = products.size();
        productCategory.setProducts(products);

        productCategoryDao.add(productCategory);

        productCategory = productCategoryDao.get(productCategory.getId());
        assertNotNull(productCategory);
        Assert.assertEquals(size, productCategory.getProducts().size());
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet() {
        ProductCategory p = productCategoryDao.get(1);
        org.junit.Assert.assertNotNull(p);
    }

    @Test
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
    public void testRemove() {
        ProductCategory p = productCategoryDao.get(3);
        productCategoryDao.remove(p);
        assertNull(productCategoryDao.get(3));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemoveWithChilds() {
        ProductCategory p = productCategoryDao.get(2);
        productCategoryDao.remove(p);
        assertNull(productCategoryDao.get(2));
    }
}
