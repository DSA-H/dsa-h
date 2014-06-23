package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.ProductCategoryDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class ProductCategoryServiceTest extends AbstractDatabaseTest {
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;

    @Test
    public void add_shouldPersistEntity() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test1");

        Set<Product> products = new HashSet<>(productService.getAll());
        int size = products.size();
        productCategory.setProducts(products);

        productCategoryService.add(productCategory);
        saveCancelService.save();

        productCategory = productCategoryService.get(productCategory.getId());
        assertNotNull(productCategory);
//        Assert.assertEquals(size, productCategory.getProducts().size());
    }

    @Test
    public void testGet() {
        ProductCategory p = productCategoryService.get(1);
        org.junit.Assert.assertNotNull(p);
    }

    @Test
    public void add_WithParent() {
        ProductCategory parent = productCategoryService.get(1);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test2");
        productCategory.setParent(parent);

        productCategoryService.add(productCategory);
        saveCancelService.save();
        productCategory = productCategoryService.get(productCategory.getId());
        assertNotNull(productCategory);
    }

    @Test(expected = DSAValidationException.class)
    public void add_WithParentCycle() {
        ProductCategory parent = productCategoryService.get(5);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("test2");
        productCategory.setParent(parent);

        parent.setParent(productCategory);

        productCategoryService.add(productCategory);
        saveCancelService.save();
        productCategoryService.update(parent);
        saveCancelService.save();
    }

    @Test
    public void testRemove() {
        ProductCategory p = productCategoryService.get(3);
        productCategoryService.remove(p);
        saveCancelService.save();
        assertNull(productCategoryService.get(3));
    }

    @Test
    public void testRemoveWithChildren() {
        ProductCategory p = productCategoryService.get(2);
        productCategoryService.remove(p);
        saveCancelService.save();
        assertNull(productCategoryService.get(2));
    }

    @Test
    public void getAll_getsAll() {
        ProductCategory c1 = productCategoryService.get(1);
        ProductCategory c2 = productCategoryService.get(2);
        ProductCategory c3 = productCategoryService.get(3);
        ProductCategory c4 = productCategoryService.get(4);
        ProductCategory c5 = productCategoryService.get(5);
        ProductCategory c6 = productCategoryService.get(6);

        List<ProductCategory> all = productCategoryService.getAll();
        assertThat(all, hasItems(c1, c2, c3, c4, c5, c6));
        assertEquals(6, all.size());
    }
}
