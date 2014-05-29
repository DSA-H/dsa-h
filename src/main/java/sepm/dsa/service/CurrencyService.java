package sepm.dsa.service;

import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.model.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyService {

    /**
     * Get a currency by its ID
     *
     * @param id the id
     * @return the currency
     */
    Currency get(int id);

    /**
     * Add a new currency to DB
     *
     * @param r currency (not null)
     * @return The created currency model.
     */
    Currency add(Currency r);

    /**
     * Update a currency
     *
     * @param r currency (must not be null)
     * @return The updated currency model.
     */
    Currency update(Currency r);

    /**
     * Removes a currency from DB
     *
     * @param r currency (must not be null)
     */
    void remove(Currency r);

    /**
     * @return all entries, might be an empty list (not null)
     */
    List<Currency> getAll();

    /**
     * Exchanges / converts from one to the other currency
     *
     * @param from   the original Currency must not be null
     * @param to     the foreign currency must not be null
     * @param amount amount of from {@code Currency} to be exchanged
     * @return the value / amount of the original currency expressed by / in the foreign currency
     */
    CurrencyAmount exchange(Currency from, Currency to, BigDecimal amount);
}
