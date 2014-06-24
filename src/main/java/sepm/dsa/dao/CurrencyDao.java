package sepm.dsa.dao;

import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;

import java.util.List;

public interface CurrencyDao extends BaseDao<Currency> {

    /**
     * @param currencySet the currencySet (not null)
     * @return all Currencies in a specific currencySet, might be an empty list (not null)
     */
    List<Currency> getAllByCurrencySet(CurrencySet currencySet);

}
