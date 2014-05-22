package sepm.dsa.dao;


import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface TraderCategoryDao {

    /**
     * Persists a {@code TraderCategory} in the Database
     *
     * @param traderCategory to be persisted must not be null
     */
   public void add(TraderCategory traderCategory);

    /**
     * Updates a already existing {@code TraderCategory} in the database
     *
     * @param traderCategory to update must not be null
     */
   public void update(TraderCategory traderCategory);

    /**
     * Delete a traderCategory permanently
     *
     * @param traderCategory to be deleted must not be null
     */
   public void remove(TraderCategory traderCategory);

    /**
     * Searches for the {@code TraderCategory} by a given id
     *
     * @return the traderCategory or throw a DSARuntimeException
     */
  public   TraderCategory get(int id);

    /**
     * Finds all TraderCategories
     *
     * @return the traderCategories or empty list of no traderCategorys exist (not null)
     */
   public List<TraderCategory> getAll();
}
