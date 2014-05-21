package sepm.dsa.service;

import sepm.dsa.model.AssortmentNature;

import java.util.HashSet;
import java.util.List;

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
    public void add(HashSet<AssortmentNature> assortmentNature);

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
     * Asks the DAO to find all AssortmentNatures
     *
     * @return the assortmentNatures or empty list of no assortmentNatures exist (not null)
     */
    public List<AssortmentNature> getAll();
}
