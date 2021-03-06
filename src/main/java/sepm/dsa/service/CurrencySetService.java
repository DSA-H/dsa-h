package sepm.dsa.service;

import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.model.CurrencySet;

import java.util.List;

public interface CurrencySetService {

    /**
     * Get a currencySet by its ID
     *
     * @param id the id
     * @return the currencySet
     */
    CurrencySet get(int id);

    /**
     * Add a new currencySet to DB
     *
     * @param r currencySet (not null)
     * @return The created currencySet model.
     */
    CurrencySet add(CurrencySet r);

    /**
     * Update a currencySet
     *
     * @param r currencySet (must not be null)
     * @return The updated currencySet model.
     */
    CurrencySet update(CurrencySet r);

    /**
     * Removes a currencySet from DB
     *
     * @param r currencySet (must not be null)
     */
    void remove(CurrencySet r);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<CurrencySet> getAll();

    /**
     *
     * @param currencySet
     * @param baseRateAmount
     * @return returns an ordered list with currency-value pairs, representing the amount of money in baseRate
     *          (order by currency valueToBaseRate)
     */
    List<CurrencyAmount> toCurrencySet(CurrencySet currencySet, Integer baseRateAmount);

    /**
     * @return the default currency set (ID=1)
     */
    CurrencySet getDefaultCurrencySet();

}
