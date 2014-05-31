package sepm.dsa.service;

import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface AssortmentNatureService {

    /**
     * Validates a AssortmentNature
     *
     * @param assortmentNature must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if assortmentNature is not valid
     */
    void validate(AssortmentNature assortmentNature) throws DSAValidationException;

    /**
     * Asks the DAO to persist a {@code AssortmentNature} in the Database
     *
     * @param assortmentNature to be persisted must not be null
     * @return The added assortmentNature model.
     */
    AssortmentNature add(AssortmentNature assortmentNature);

    /**
     * Asks the DAO to update a already existing {@code AssortmentNature} in the database
     *
     * @param assortmentNature to update must not be null
     * @return The updated assortmentNature model.
     */
    AssortmentNature update(AssortmentNature assortmentNature);

    /**
     * Asks the DAO to delete a assortmentNature permanently
     *
     * @param assortmentNature to be deleted must not be null
     */
    void remove(AssortmentNature assortmentNature);

    /**
     * Gets a assortmentNature by its id
     *
     * @param traderCategory
     * @param productCategory
     * @return the assortmentNature, or null if it does not exist
     */
    AssortmentNature get(TraderCategory traderCategory, ProductCategory productCategory);

    /**
     * Asks the DAO to find all AssortmentNatures
     *
     * @return the assortmentNatures or empty list of no assortmentNatures exist (not null)
     */
    List<AssortmentNature> getAll();

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
