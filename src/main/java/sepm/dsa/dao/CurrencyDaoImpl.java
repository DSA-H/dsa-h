package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CurrencyDaoImpl implements CurrencyDao {

    private static final Logger log = LoggerFactory.getLogger(CurrencyDaoImpl.class);
    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(Currency currency) {
        log.debug("calling add(" + currency + ")");
        sessionFactory.getCurrentSession().save(currency);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Currency currency) {
        log.debug("calling update(" + currency + ")");
        sessionFactory.getCurrentSession().update(currency);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Currency currency) {
        log.debug("calling remove(" + currency + ")");
        sessionFactory.getCurrentSession().delete(currency);
    }

    @Override
    public Currency get(int id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(Currency.class, id);

        if (result == null) {
            return null;
        }
        log.trace("returning " + result);
        return (Currency) result;
    }

    @Override
    public List<Currency> getAll() {
        log.debug("calling getAll()");
        List<Currency> result = new ArrayList<>();

        sessionFactory.getCurrentSession()
                .getNamedQuery("Currency.findAll")
                .list()
                .forEach(o -> result.add((Currency) o));

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
