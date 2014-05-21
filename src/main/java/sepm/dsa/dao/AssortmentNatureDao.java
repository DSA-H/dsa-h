package sepm.dsa.dao;

import sepm.dsa.model.AssortmentNature;

import java.util.List;

public interface AssortmentNatureDao {

    /**
     * Persists a {@code AssortmentNature} in the Database
     *
     * @param assortmentNature to be persisted must not be null
     * @return
     */
    public void add(AssortmentNature assortmentNature);

    /**
     * Updates a already existing {@code sepm.dsa.model.AssortmentNature} in the database
     *
     * @param assortmentNature to update must not be null
     */
    public void update(AssortmentNature assortmentNature);

    /**
     * Delete a assortmentNature permanently
     *
     * @param assortmentNature to be deleted must not be null
     */
    public void remove(AssortmentNature assortmentNature);

    AssortmentNature get(int id);

    /**
     * Finds all sepm.dsa.model.AssortmentNatures
     *
     * @return the locations or empty list of no locations exist (not null)
     */
    public List<AssortmentNature> getAll();
}
