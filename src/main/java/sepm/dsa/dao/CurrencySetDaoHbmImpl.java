package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;

@Transactional(readOnly = true)
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
