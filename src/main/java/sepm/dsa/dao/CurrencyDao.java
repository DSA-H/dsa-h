package sepm.dsa.dao;

import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;

import java.util.List;

public interface CurrencyDao extends BaseDao<Currency> {

    List<Currency> getAllByCurrencySet(CurrencySet currencySet);

}
