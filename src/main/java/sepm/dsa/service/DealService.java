package sepm.dsa.service;

import sepm.dsa.model.Deal;

import java.util.List;

public interface DealService {

    /**
     * Get a deal by its ID
     *
     * @param id the id
     * @return the deal
     */
    Deal get(int id);

    /**
     * Add a new deal to DB
     *
     * @param r deal (not null)
     * @return The created deal model.
     */
    Deal add(Deal r);

    /**
     * Update a deal
     *
     * @param r deal (must not be null)
     * @return The updated deal model.
     */
    Deal update(Deal r);

    /**
     * Removes a deal from DB
     *
     * @param r deal (must not be null)
     */
    void remove(Deal r);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Deal> getAll();
}
