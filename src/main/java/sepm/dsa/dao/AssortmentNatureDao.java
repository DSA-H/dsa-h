package sepm.dsa.dao;

import sepm.dsa.model.AssortmentNature;

import java.util.List;

public interface AssortmentNatureDao extends BaseDao<AssortmentNature> {

    /**
     * Gets all {@code AssortmentNature}s by its TraderCategory
     *
     * @param traderCategoryId
     * @return a list of AssortmentNatures, might be empty (not null).
     */
    List<AssortmentNature> getAllByTraderCategory(int traderCategoryId);

    /**
     * Gets all {@code AssortmentNature}s by its ProductCategory
     *
     * @param productCategoryId
     * @return a list of AssortmentNatures, might be empty (not null).
     */
    List<AssortmentNature> getAllByProductCategory(int productCategoryId);

}
