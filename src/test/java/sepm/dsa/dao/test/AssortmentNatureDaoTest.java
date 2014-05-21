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
import sepm.dsa.dao.AssortmentNatureDao;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.service.ProductCategoryService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class AssortmentNatureDaoTest {

    @Autowired
    AssortmentNatureDao assortmentNatureDao;
    @Autowired
    ProductCategoryService productCategoryService;

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        ProductCategory p1 = productCategoryService.get(1);
        AssortmentNature assortmentNature = new AssortmentNature();

        assortmentNature.setDefaultOccurence(40);
        assortmentNature.setProductCategory(p1);

        assortmentNatureDao.add(assortmentNature);
        assertNotNull(assortmentNatureDao.get(assortmentNature.getId()));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        AssortmentNature assortmentNature = assortmentNatureDao.get(1);
        assortmentNatureDao.remove(assortmentNature);
        assertEquals(null, assortmentNatureDao.get(1));
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        assortmentNatureDao.getAll();
        assertEquals(assortmentNatureDao.getAll().size(), 2);
    }
}