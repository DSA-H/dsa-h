package sepm.dsa.service;

import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface TraderCategoryService {

    /**
     * Searches for the {@code TraderCategory} by a specified id
     *
     * @return the traderCategory or null if nothing found
     */
    TraderCategory get(int id);

    /**
     * Persists a {@code TraderCategory} in the Database
     *
     * @param t to be persisted must not be null
     * @return The added traderCategory model.
     */
    TraderCategory add(TraderCategory t);

    /**
     * Updates a already existing {@code TraderCategory} in the database
     *
     * @param t to update must not be null
     * @return The updated traderCategory model.
     */
    TraderCategory update(TraderCategory t);

    /**
     * Delete a traderCategory permanently
     *
     * @param t to be deleted must not be null
     */
    void remove(TraderCategory t);

    /**
     * Finds all TraderCategories
     *
     * @return the traderCategories or empty list if no traderCategories exist
     */
    List<TraderCategory> getAll();
}
