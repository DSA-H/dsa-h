package sepm.dsa.dao;

import org.hibernate.Query;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;

import java.util.List;
import java.util.Vector;

public class CurrencyDaoImpl
	extends BaseDaoHbmImpl<Currency>
	implements CurrencyDao {


    @Override
    public Currency add(Currency model) {
        Currency result = super.add(model);
        for (CurrencySet c : result.getCurrencySets()) {
            c.getCurrencies().add(result);
        }
        return result;
    }

    @Override
    public void remove(Currency model) {
        model = (Currency) sessionFactory.getCurrentSession().get(Currency.class, model.getId());
        sessionFactory.getCurrentSession().refresh(model);
        super.remove(model);
        for (CurrencySet c : model.getCurrencySets()) {
            c.getCurrencies().remove(model);
        }
    }

    @Override
    public List<Currency> getAllByCurrencySet(CurrencySet currencySet) {
        log.debug("calling getAllByCurrencySet(" + currencySet + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Currency.findAllByCurrencySet");
        query.setParameter("currencySetId", currencySet == null ? null : currencySet.getId());
        List<?> list = query.list();

        List<Currency> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Currency) o);
        }

        log.trace("returning " + result);
        return result;
    }


}
