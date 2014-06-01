package sepm.dsa.service.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.AssortmentNatureService;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.TraderCategoryService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Transactional
public class AssortmentNatureServiceTest extends AbstractDatabaseTest {

    @Autowired
    private AssortmentNatureService assortmentNatureService;


    @Autowired
    private TraderCategoryService traderCategoryService;

    @Autowired
    private ProductCategoryService productCategoryService;


    @Test
    public void add_shouldPersistEntity() {

        int sizeBefore = assortmentNatureService.getAll().size();

        TraderCategory traderCategory = traderCategoryService.get(4);
        ProductCategory productCategory = productCategoryService.get(5);

        AssortmentNature assortmentNature = new AssortmentNature();
        assortmentNature.setTraderCategory(traderCategory);
        assortmentNature.setProductCategory(productCategory);
        assortmentNature.setDefaultOccurence(50);

        AssortmentNature assortmentNatureBefore = assortmentNatureService.get(traderCategory, productCategory);

        assortmentNatureService.add(assortmentNature);
        getSaveCancelService().save();

        assertNull(assortmentNatureBefore);
        assertNotNull(assortmentNatureService.get(traderCategory, productCategory));

        assertEquals(sizeBefore + 1, assortmentNatureService.getAll().size());
    }

    @Test
    public void update_shouldUpdateEntity() {
        TraderCategory traderCategory = traderCategoryService.get(1);
        ProductCategory productCategory = productCategoryService.get(2);
        AssortmentNature assortmentNature = assortmentNatureService.get(traderCategory, productCategory);

        assortmentNature.setDefaultOccurence(50);
        assortmentNatureService.update(assortmentNature);

        AssortmentNature assortmentNatureAfter = assortmentNatureService.get(traderCategory, productCategory);

        assertEquals(new Integer(50), assortmentNatureAfter.getDefaultOccurence());
    }

    @Test
    public void remove_shouldRemoveEntity() {
        int sizeBefore = assortmentNatureService.getAll().size();

        TraderCategory traderCategory = traderCategoryService.get(1);
        ProductCategory productCategory = productCategoryService.get(2);
        AssortmentNature assortmentNature = assortmentNatureService.get(traderCategory, productCategory);

        assortmentNatureService.remove(assortmentNature);
        getSaveCancelService().save();

        assertEquals(sizeBefore - 1, assortmentNatureService.getAll().size());
        assertNull(assortmentNatureService.get(traderCategory, productCategory));

    }

    @Test
    public void get_shouldRetrieveEntity() {
        TraderCategory traderCategory = traderCategoryService.get(1);
        ProductCategory productCategory = productCategoryService.get(2);
        AssortmentNature assortmentNature = assortmentNatureService.get(traderCategory, productCategory);

        assertNotNull(assortmentNature);
    }

    @Test
    public void getAll_shouldRetrieveAllEntities() {
        List<AssortmentNature> all = assortmentNatureService.getAll();

        assertEquals(9, all.size());
    }
    
}
