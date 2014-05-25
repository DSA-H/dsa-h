package sepm.dsa.service;


import sepm.dsa.model.*;

import java.util.Collection;
import java.util.List;

public interface TraderService {

    /**
     * Gets a {@code Trader} by it's id
     *
     * @param id must not be null
     * @return the Trader, or null if it does not exist
     */
    Trader get(int id);

    /**
     * Persists a {@code Trader} in the Database
     *
     * @param t (Trader) to be persisted must not be null
     * @return
     */
    void add(Trader t);

    /**
     * Updates a already existing {@code Trader} in the database
     *
     * @param t to update must not be null
     */
    void update(Trader t);

    /**
     * Delete a {@code Trader} permanently from the DB
     *
     * @param t to be deleted must not be null
     */
    void remove(Trader t);

    /**
     * Get all traders for a specified location or empty List if nothing found
     *
     * @param location must not be null
     * @return the traders for the location or empty list
     */
    List<Trader> getAllForLocation(Location location);

    /**
     * Get all traders for a specified category.
     *
     * @param category the filter string (TraderCategory name), must not be null
     * @return the list of traders matching this category or empty list if nothing found
     */
    List<Trader> getAllByCategory(TraderCategory category);

    /**
     * Generates the offers and then calculates their pricings for a specified trader (https://sepm.bountin.net/confluence/pages/viewpage.action?pageId=3375469) according to these rules
     *
     * @param trader the trader to calculate offers for -- must not be null
     * @return a list of offers for the specified trader
     */
    List<Offer> calculateOffers(Trader trader);

    /**
     * Calculates the price for a product --> needed for offer calculation
     *
     * @param product the product to calculate the offer for - must not be null
     * @param trader  the trader
     * @return the price in default (base-rate) currency
     */
    int calculatePriceForProduct(Product product, Trader trader);

	/**
	 * Returns a list of Offers of the specified trader.
	 *
	 * @param trader the trader whose offers are requested.
	 * @return a list of the trader's offers.
	 */
	Collection<Offer> getOffers(Trader trader);
}
