package sepm.dsa.dao;

import sepm.dsa.model.Currency;

import java.util.List;

public interface CurrencyDao {
    /**
     * Persists a {@code Currency} in the Database
     *
     * @param currency to be persisted must not be null
     * @return
     */
    void add(Currency currency);

    /**
     * Updates a already existing {@code Currency} in the database
     *
     * @param currency to update must not be null
     */
    void update(Currency currency);

    /**
     * Delete a currency permanently
     *
     * @param currency to be deleted must not be null
     */
    void remove(Currency currency);

    Currency get(int id);

    /**
     * Finds all Currencies
     *
     * @return the currencies or empty list of no currencies exist (not null)
     */
    List<Currency> getAll();
}
