package sepm.dsa.dao;

import sepm.dsa.model.Trader;

import java.util.List;

/**
 * Created by Jotschi on 18.05.2014.
 */
public interface TraderDao {

    /**
     * Persists a {@code Trader} in the Database
     *
     * @param trader to be persisted must not be null
     * @return
     */
    void add(Trader trader);

    /**
     * Updates a already existing {@code Trader} in the database
     *
     * @param trader to update must not be null
     */
    void update(Trader trader);

    /**
     * Delete a trader permanently
     *
     * @param trader to be deleted must not be null
     */
    void remove(Trader trader);

    /**
     * Finds all Traders
     *
     * @return the traders or empty list of no traders exist (not null)
     */
    Trader get(int id);

    /**
     * Finds all Traders
     *
     * @return the traders or empty list of no traders exist (not null)
     */
    List<Trader> getAllByTrader(Trader trader);
}
