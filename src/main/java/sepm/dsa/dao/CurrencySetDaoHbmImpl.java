package sepm.dsa.dao;

import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;

public class CurrencySetDaoHbmImpl extends BaseDaoHbmImpl<CurrencySet> implements CurrencySetDao {

    @Override
    public CurrencySet add(CurrencySet model) {
        CurrencySet result = super.add(model);
        for (Currency c : result.getCurrencies()) {
            c.getCurrencySets().add(result);
        }
        return result;
    }

    @Override
    public void remove(CurrencySet model) {
        super.remove(model);
        for (Currency c : model.getCurrencies()) {
            c.getCurrencySets().remove(model);
        }
    }
}
