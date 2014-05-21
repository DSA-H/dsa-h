package sepm.dsa.dao.test;

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
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jotschi on 18.05.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class ProductDaoTest {

    @Autowired
    private ProductDao productDao;

    @Test
    @DatabaseSetup("/testData.xml")
    public void add_shouldPersistEntity() {
        Product product = new Product();
        product.setName("Testproduct1");
        product.setAttribute(ProductAttribute.LAGERBAR);
        product.setQuality(false);
        product.setComment("test12345 Kommentar");
        product.setCost(10);

        productDao.add(product);

        Product persistedproduct = productDao.get(product.getId());
        assertTrue(persistedproduct != null);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetProduct() {
        Product p = productDao.get(2);
        assertEquals(new Integer(2), p.getId());
    }

    @Test(expected = org.hibernate.PropertyValueException.class)
    public void add_incompleteProduct_shouldNOTPersistEntity() {

        Product product = new Product();
        productDao.add(product);
        Product persistedProduct = productDao.get(product.getId());
        assertTrue(persistedProduct == null);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemoveProduct() {
        Product p = productDao.get(2);
        int size = productDao.getAll().size();
        productDao.remove(p);
        assertEquals(size, productDao.getAll().size()+1);
    }

}
