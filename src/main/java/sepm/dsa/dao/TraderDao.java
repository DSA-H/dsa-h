package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.List;

/**
 * Created by Jotschi on 18.05.2014.
 */
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
     * @return the trader or throw a DSARuntimeException;
     */
    public Trader get(int id);

    /**
     * Finds all Traders
     *
     * @return the traders or empty list of no traders exist (not null)
     */
    public List<Trader> getAllByLocation(Location location);

    public List<Trader> getAllByCategory(TraderCategory category);
}
