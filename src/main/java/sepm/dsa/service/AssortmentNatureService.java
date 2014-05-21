package sepm.dsa.service;

import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface AssortmentNatureService {

    /**
     * Asks the DAO to persist a {@code AssortmentNature} in the Database
     *
     * @param assortmentNature to be persisted must not be null
     * @return
     */
    public void add(AssortmentNature assortmentNature);

    /**
     * Asks the DAO to persist a HashSet of {@code AssortmentNature} in the Database
     *
     * @param assortmentNature to be persisted must not be null
     * @return
     */
    public void add(Set<AssortmentNature> assortmentNature);

    /**
     * Asks the DAO to update a already existing {@code AssortmentNature} in the database
     *
     * @param assortmentNature to update must not be null
     */
    public void update(AssortmentNature assortmentNature);

    /**
     * Asks the DAO to delete a assortmentNature permanently
     *
     * @param assortmentNature to be deleted must not be null
     */
    public void remove(AssortmentNature assortmentNature);

    AssortmentNature get(int id);

    /**
     * Validates a Location
     *
     * @param assortmentNature must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if assortmentNature is not valid
     */
    public void validate(AssortmentNature assortmentNature) throws DSAValidationException;

    /**
     * Asks the DAO to find all AssortmentNatures
     *
     * @return the assortmentNatures or empty list of no assortmentNatures exist (not null)
     */
    public List<AssortmentNature> getAll();
}
