package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.AssortmentNatureDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.TraderCategoryService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Transactional
public class AssortmentNatureDaoTest extends AbstractDatabaseTest {

    @Autowired
    private AssortmentNatureDao assortmentNatureDao;

    @Autowired
    private TraderCategoryService traderCategoryService;

    @Autowired
    private ProductCategoryService productCategoryService;


    @Test
    public void add_shouldPersistEntity() {

        int sizeBefore = assortmentNatureDao.getAll().size();

        TraderCategory traderCategory = traderCategoryService.get(4);
        ProductCategory productCategory = productCategoryService.get(5);

        AssortmentNature assortmentNature = new AssortmentNature();
        assortmentNature.setTraderCategory(traderCategory);
        assortmentNature.setProductCategory(productCategory);
        assortmentNature.setDefaultOccurence(50);

        AssortmentNature assortmentNatureBefore = assortmentNatureDao.get(new AssortmentNature.Pk(traderCategory, productCategory));

        assortmentNatureDao.add(assortmentNature);
        getSaveCancelService().save();

        assertNull(assortmentNatureBefore);
        assertNotNull(assortmentNatureDao.get(new AssortmentNature.Pk(traderCategory, productCategory)));

        assertEquals(sizeBefore + 1, assortmentNatureDao.getAll().size());
    }

    @Test
    public void update_shouldUpdateEntity() {
        TraderCategory traderCategory = traderCategoryService.get(1);
        ProductCategory productCategory = productCategoryService.get(2);
        AssortmentNature.Pk pk = new AssortmentNature.Pk(traderCategory, productCategory);
        AssortmentNature assortmentNature = assortmentNatureDao.get(pk);

        assortmentNature.setDefaultOccurence(200);
        assortmentNatureDao.update(assortmentNature);

        AssortmentNature assortmentNatureAfter = assortmentNatureDao.get(pk);

        assertEquals(new Integer(200), assortmentNatureAfter.getDefaultOccurence());
    }

    @Test
    public void remove_shouldRemoveEntity() {
        int sizeBefore = assortmentNatureDao.getAll().size();

        TraderCategory traderCategory = traderCategoryService.get(1);
        ProductCategory productCategory = productCategoryService.get(2);
        AssortmentNature.Pk pk = new AssortmentNature.Pk(traderCategory, productCategory);
        AssortmentNature assortmentNature = assortmentNatureDao.get(pk);

        assortmentNatureDao.remove(assortmentNature);
        getSaveCancelService().save();

        assertEquals(sizeBefore - 1, assortmentNatureDao.getAll().size());
        assertNull(assortmentNatureDao.get(pk));

    }

    @Test
    public void get_shouldRetrieveEntity() {
        TraderCategory traderCategory = traderCategoryService.get(1);
        ProductCategory productCategory = productCategoryService.get(2);
        AssortmentNature.Pk pk = new AssortmentNature.Pk(traderCategory, productCategory);
        AssortmentNature assortmentNature = assortmentNatureDao.get(pk);

        assertNotNull(assortmentNature);
    }

    @Test
    public void getAll_shouldRetrieveAllEntities() {
        List<AssortmentNature> all = assortmentNatureDao.getAll();

        assertEquals(9, all.size());
    }


}
