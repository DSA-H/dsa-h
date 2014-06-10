package sepm.dsa.service;


import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.model.*;

import java.math.BigDecimal;
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
     * @return The added trader model.
     */
    Trader add(Trader t);

    /**
     * Updates a already existing {@code Trader} in the database
     *
     * @param t to update must not be null
     * @return The updated trader model.
     */
    Trader update(Trader t);

    /**
     * Delete a {@code Trader} permanently from the DB
     *
     * @param t to be deleted must not be null
     */
    void remove(Trader t);

    /**
     * Get all traders
     * @return the traders or empty list
     */
    List<Trader> getAll();

    /**
     * Get all moving traders
     * @return the moving traders or empty list
     */
    List<MovingTrader> getAllMovingTraders();

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
     * Generates the offers and then calculates their pricings for a specified trader (https://sepm.bountin.net/confluence/pages/viewpage.action?pageId=3375469) according to these rules
     *
     * @param trader the trader to calculate offers for -- must not be null
     * @param number the number of Offers that calculated
     * @return a list of offers for the specified trader
     */
    List<Offer> calculateOffers(Trader trader, int number);

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

    Trader recalculateOffers(Trader t);

    /**
     *
     * @param productQuality
     * @param product
     * @param trader
     * @return
     */
    int calculatePricePerUnit(ProductQuality productQuality, Product product, Trader trader);

    /**
     * Recalculates the prices for all offers of a trader
     *
     * @param trader the trader
     * @return the price in default (base-rate) currency
     */
    void reCalculatePriceForOffer(/*Set<Offer> offers, */Trader trader);

    /**
     * Recalculates the price for all offers of a trader if the new price of the product is higher
     *
     * @param trader the trader
     * @return the price in default (base-rate) currency
     */
    void reCalculatePriceForOfferIfNewPriceIsHigher(/*Set<Offer> offers, */Trader trader);

    /**
     * A trader sells a product to a player. The trader's amount for this product decreases. If the amount becomes zero,
     * then the trader's corresponding offer will be removed.
     *
     * @param trader
     * @param player
     * @param product
     * @param unit the unit of the product
     * @param amount product amount, > 0
     * @param totalPrice total price for this deal in base rate
     * @param discount discount int percent [0..100]
     * @throws sepm.dsa.exceptions.DSAValidationException if trader does not have the product with this quality <br />
     *      or the amount is greater than the trader offers <br />
     *      or unit type does does not match the product unit type <br />
     *      or totalPrice is negative
     */
    Deal sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, Integer totalPrice, Integer discount);

    /**
     * A trader sells a product to a player. The trader's amount for this product decreases. If the amount becomes zero,
     * then the trader's corresponding offer will be removed.
     *
     * @param trader
     * @param player
     * @param product
     * @param unit the unit of the product
     * @param amount product amount, > 0
     * @param totalPrice total price for this deal in multiple currencies (e.g. deriving from a currency set)
     * @param discount discount int percent [0..100]
     *
     * @return converts the total price to base rate and calls 'Deal sellToPlayer(Trader, Player, Product, ProductQuality, Unit, Integer, Integer)'
     *
     * @throws sepm.dsa.exceptions.DSAValidationException if trader does not have the product with this quality <br />
     *      or the amount is greater than the trader offers <br />
     *      or unit type does does not match the product unit type <br />
     *      or totalPrice is negative
     */
    Deal sellToPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, List<CurrencyAmount> totalPrice, Integer discount);

    /**
     * A trader buys a product from a player. The trader's amount for this product increases
     *
     * @param trader
     * @param player
     * @param product
     * @param unit the unit of the product
     * @param amount product amount, > 0
     * @param totalPrice total price for this deal
     *
     * @throws sepm.dsa.exceptions.DSAValidationException if trader does not have the product with this quality <br />
     *      or the amount is greater than the trader offers <br />
     *      or unit type does does not match the product unit type <br />
     *      or totalPrice is negative
     */
    Deal buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, Integer totalPrice);

    /**
     * @param trader
     * @param player
     * @param product
     * @param productQuality
     * @param unit the unit of the product
     * @param amount product amount, > 0
     * @param totalPrice total price for this deal in multiple currencies (e.g. deriving from a currency set)
     * @return converts the total price to base rate and calls Deal buyFromPlayer(Trader, Player, Product, ProductQuality, Unit, Integer, Integer);
     */
    Deal buyFromPlayer(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount, List<CurrencyAmount> totalPrice);

    /**
     * Suggests a discount for a Players Product purchase in percent (0 ... 0%, 20 ... 20%, etc).
     *
     * @param trader the Trader
     * @param player the Player who buys the product
     * @param product the Product
     * @param productQuality the Product Quality
     * @param unit the Product Unit
     * @param amount the Amount of Products
     * @return the suggested discount for the purchase [0..100], meaning 0 ... 0% discount (full price), 100 ... 100% discount (free)
     */
    Integer suggesstDiscount(Trader trader, Player player, Product product, ProductQuality productQuality, Unit unit, Integer amount);


}
