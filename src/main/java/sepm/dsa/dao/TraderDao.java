package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface TraderDao extends BaseDao<Trader> {
    /**
     * Finds all Traders
     *
     * @return the traders or empty list of no traders exist (not null)
     */
    public List<Trader> getAllByLocation(Location location);

    /**
     * Get all traders for a specified category.
     *
     * @param category the filter string (TraderCategory name), must not be null
     * @return the list of traders matching this category or empty list if nothing found
     */
    public List<Trader> getAllByCategory(TraderCategory category);
}
