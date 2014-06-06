package sepm.dsa.dao.test;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.dao.UnitDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductAttribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductDaoTest extends AbstractDatabaseTest {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private UnitDao unitDao;

    @Test
    public void add_shouldPersistEntity() {
        Product product = new Product();
        product.setName("Testproduct1");
        product.setAttribute(ProductAttribute.LAGERBAR);
        product.setQuality(false);
        product.setComment("test12345 Kommentar");
        product.setCost(10);
        product.setUnit(unitDao.get(4));

        productDao.add(product);

        Product persistedproduct = productDao.get(product.getId());
        assertNotNull(persistedproduct);
    }

    @Test
    public void testGetProduct() {
        Product p = productDao.get(1);
        assertEquals(new Integer(1), p.getId());
    }

    @Test(expected = org.hibernate.PropertyValueException.class)
    public void add_incompleteProduct_shouldNOTPersistEntity() {
        Product product = new Product();
        productDao.add(product);
        Product persistedProduct = productDao.get(product.getId());
        assertTrue(persistedProduct == null);
    }

    @Test
    public void testRemoveProduct() {
        Product p = productDao.get(2);
        int size = productDao.getAll().size();
        productDao.remove(p);
	    saveCancelService.save();
        assertEquals(size - 1, productDao.getAll().size());
    }

    @Test
    public void getAllByName_nullParamReturnsAll() {

        Set<Product> allByName = new HashSet<>(productDao.getAllByName(null));
        Set<Product> all = new HashSet<>(productDao.getAll());

        Assert.assertEquals(all, allByName);
    }

    @Test
    public void getAllByName_shouldFilterOthers() {

        Set<Product> allByName = new HashSet<>(productDao.getAllByName("%1%"));

        Assert.assertEquals(1, allByName.size());
    }


}
