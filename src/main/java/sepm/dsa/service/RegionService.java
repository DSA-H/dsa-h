package sepm.dsa.service;

import sepm.dsa.model.CurrencySet;
import sepm.dsa.model.Region;

import java.util.List;

public interface RegionService {

    /**
     * Get a region by its ID
     *
     * @param id the id
     * @return the region
     */
    Region get(int id);

    /**
     * Add a new region to DB
     *
     * @param r region (not null)
     * @return The added region model.
     */
    Region add(Region r);

    /**
     * Update a region
     *
     * @param r region (must not be null)
     * @return The updated region model.
     */
    Region update(Region r);

    /**
     * Removes a region from DB and also all connected region borders
     *
     * @param r region (must not be null)
     * @see sepm.dsa.model.RegionBorder
     */
    void remove(Region r);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Region> getAll();

    /**
     * @param currencySet the preferred Currency Set (not null)
     * @return all Region with this preferred CurrencySet, might be an empty list (not null)
     */
    List<Region> getAllByPreferredCurrencySet(CurrencySet currencySet);


}
