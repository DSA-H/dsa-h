package sepm.dsa.dao;


import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARegionNotExistingException;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class TraderDaoHbmImpl implements TraderDao {

    private static final Logger log = LoggerFactory.getLogger(TraderDaoHbmImpl.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(Trader trader) {
        log.debug("calling add(" + trader + ")");
        sessionFactory.getCurrentSession().save(trader);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Trader trader) {
        log.debug("calling update(" + trader + ")");
        sessionFactory.getCurrentSession().update(trader);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Trader trader) {
        log.debug("calling remove(" + trader + ")");
        sessionFactory.getCurrentSession().delete(trader);
    }

    @Override
    public Trader get(int id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(Trader.class, id);

        if (result == null) {
            log.trace("returning " + null);
            return null;
        }
        log.trace("returning " + result);
        return (Trader) result;
    }

    @Override
    public List<Trader> getAllByLocation(Location location) {
        log.debug("calling getAllByLocation("+location+")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("trader.getAllForLocation")
                .setParameter("location", location)
                .list();

        List<Trader> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Trader) o);
        }

	    log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Trader> getAllByCategory(TraderCategory category) {
        log.debug("calling getAllByCategory("+category+")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("trader.getAllForCategory")
                .setParameter("category", category)
                .list();

        List<Trader> result = new ArrayList<>(list.size());
        for (Object o : list) {
            result.add((Trader) o);
        }

	    log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory");
        this.sessionFactory = sessionFactory;
    }
}
