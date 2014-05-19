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
import sepm.dsa.dao.RegionDao;
import sepm.dsa.model.*;

import static org.junit.Assert.assertTrue;

import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;
import sepm.dsa.service.RegionService;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Chris on 17.05.2014.
 */
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
    @DatabaseSetup("/productData.xml")
    public void testXML()
    {


    }

    @Test
    @DatabaseSetup("/productData.xml")
    public void testAdd()
    {
        Product p = new Product();
        p.setName("tester");
        p.setQuality(false);
        p.setCost(1);
        p.setAttribute(ProductAttribute.LAGERBAR);

        //p.setAttribute();
        int size = productService.getAll().size();
        int id = productService.add(p);
        assertTrue(productService.getAll().size()-1 == size);
        Product newP = productService.get(id);
        assertTrue(p.equals(newP));
    }

    @Test
    @DatabaseSetup("/productData.xml")
    public void testAddWithRegion()
    {
        Product p = new Product();
        p.setName("tester");
        p.setQuality(false);
        p.setCost(1);
        p.setAttribute(ProductAttribute.LAGERBAR);

        Region r1 = regionService.get(1);
        Set<Region> rl = new HashSet<>();
        rl.add(r1);
        p.setRegions(rl);

        //p.setAttribute();
        int size = productService.getAll().size();
        int id = productService.add(p);
        assertTrue(productService.getAll().size()-1 == size);
        Product newP = productService.get(id);
        assertTrue(p.equals(newP));
        assertTrue(p.getRegions().size() == 1);
    }

    @Test
    @DatabaseSetup("/productData.xml")
    public void testRemove()
    {
        Product p = new Product();
        p.setName("tester");
        p.setQuality(false);
        p.setCost(1);
        p.setAttribute(ProductAttribute.LAGERBAR);

        Region r1 = regionService.get(1);
        Set<Region> rl = new HashSet<>();
        rl.add(r1);
        p.setRegions(rl);

        //p.setAttribute();
        int size = productService.getAll().size();
        int id = productService.add(p);
        assertTrue(productService.getAll().size()-1 == size);
        Product newP = productService.get(id);
        assertTrue(p.equals(newP));
        assertTrue(p.getRegions().size() == 1);
    }


}