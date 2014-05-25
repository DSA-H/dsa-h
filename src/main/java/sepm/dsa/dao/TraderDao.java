package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface TraderDao {

    /**
     * Persists a {@code Trader} in the Database
     *
     * @param trader to be persisted must not be null
     */
    public void add(Trader trader);

    /**
     * Updates a already existing {@code Trader} in the database
     *
     * @param trader to update must not be null
     */
    public void update(Trader trader);

    /**
     * Delete a trader permanently
     *
     * @param trader to be deleted must not be null
     */
    public void remove(Trader trader);

    /**
     * Finds all Traders
     *
     * @return the trader or null if the Trader does not exist
     */
    public Trader get(int id);

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
