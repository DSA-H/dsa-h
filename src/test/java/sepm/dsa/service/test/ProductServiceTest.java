package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductAttribute;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;
import sepm.dsa.service.RegionService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductServiceTest extends AbstractDatabaseTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private RegionService regionService;


    @Test
    public void testGet()  {
        Product p = productService.get(1);
        assertTrue(p != null);
    }

    @Test
    public void testAdd()
    {
        Product p = new Product();
        p.setName("tester2");
        p.setQuality(false);
        p.setCost(1);
        p.setAttribute(ProductAttribute.LAGERBAR);

        int size = productService.getAll().size();
        productService.add(p);
        assertTrue (productService.getAll().size()-1 == size);
        Product newP = productService.get(p.getId());
        assertEquals(p,newP);
    }

    @Test
    @Transactional(readOnly = false)
    public void testAddRegions()  {
        Product p = new Product();
        p.setName("tester");
        p.setQuality(false);
        p.setCost(1);
        p.setAttribute(ProductAttribute.LAGERBAR);

        Set<Region> regionSet = new HashSet<Region>(regionService.getAll());
        p.getRegions().addAll(regionSet);

        int size = productService.getAll().size();
        productService.add(p);
        assertTrue (productService.getAll().size()-1 == size);
        Product newP = productService.get(p.getId());
        assertEquals(p,newP);
        Set<Region> l1 = newP.getRegions();
        Set<Region> l2 = p.getRegions();
        assertEquals(l1,l2);
    }

    @Test
    public void testAddCategories()
    {
        Product p = new Product();
        p.setName("tester");
        p.setQuality(false);
        p.setCost(1);
        p.setAttribute(ProductAttribute.LAGERBAR);

        List<ProductCategory> categoryList = productCategoryService.getAll();
        Set<ProductCategory> categorySet = new HashSet<ProductCategory>();

        for (int i = 0; i<categoryList.size(); i++) {
            categorySet.add(categoryList.get(i));
        }

        p.setCategories(categorySet);

        int size = productService.getAll().size();
        productService.add(p);
        assertTrue (productService.getAll().size()-1 == size);
        Product newP = productService.get(p.getId());
        assertEquals(p, newP);
        assertEquals(p.getCategories(),newP.getCategories());
    }

    @Test
    public void testRemove()
    {
        int size = productService.getAll().size();
        Product p = productService.get(1);
        productService.remove(p);
        assertTrue(size-1 == productService.getAll().size());
    }

    @Test
    public void testUpdate()
    {
        int size = productService.getAll().size();
        Product p = productService.get(1);
        p.setName("testerUpdate");
        p.setComment("testerComment");
        productService.update(p);
        assertTrue(size == productService.getAll().size());
        Product updateProduct = productService.get(1);
        assertEquals(p,updateProduct);
    }
}