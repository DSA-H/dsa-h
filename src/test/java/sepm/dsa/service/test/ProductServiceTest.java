package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductAttribute;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;
import sepm.dsa.service.RegionService;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private RegionService regionService;


    @Test
    @DatabaseSetup("/testData.xml")
    public void testGet()  {
        Product p = productService.get(1);
        assertTrue(p != null);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd()
    {
        System.out.println(productService.getAll().toString());
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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
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
    @DatabaseSetup("/testData.xml")
    public void testRemove()
    {
        int size = productService.getAll().size();
        Product p = productService.get(1);
        productService.remove(p);
        assertTrue(size-1 == productService.getAll().size());
    }

    @Test
    @DatabaseSetup("/testData.xml")
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