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

import java.util.ArrayList;
import java.util.List;

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


    @Test
    @DatabaseSetup("/productData.xml")
    public void testXML()
    {
        System.out.println(productService.getAll().toString());
    }

    @Test
    @DatabaseSetup("/productData.xml")
    public void testAdd()
    {

        System.out.println(productService.getAll().toString());
        /*Product p = new Product();
        p.setName("tester");
        p.setQuality(false);
        p.setCost(1);

        p.setAttribute(ProductAttribute.LAGERBAR);

        List<ProductCategory> productCategories = productCategoryService.getAll();
        p.setCategories(productCategories);

        System.out.println(p.getCategories());
        p.setId(productService.add(p));
        Product compareProduct = productService.get(p.getId());
        System.out.println(compareProduct.getCategories());*/
    }


}