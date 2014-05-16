package sepm.dsa.service;

import sepm.dsa.model.Region;

import java.util.List;

public interface RegionService {

    /**
     * Get a region by its ID
     * @param id the id
     * @return the region
     */
    Region get(int id);

    /**
     * Add a new region to DB
     * @param r region (not null)
     */
    void add(Region r);

    /**
     * Update a region
     * @param r region (not null)
     */
    void update(Region r);

    /**
     * Removes a region from DB and also all connected region borders
     * @param r region (not null)
     * @see sepm.dsa.model.RegionBorder
     */
    void remove(Region r);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Region> getAll();
}
