package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class TraderCategoryDaoImpl implements TraderCategoryDao {

    private static final Logger log = LoggerFactory.getLogger(TraderCategoryDaoImpl.class);

    @Autowired
    private TraderDao traderDao;

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(TraderCategory traderCategory) {
        log.debug("calling add(" + traderCategory + ")");
        sessionFactory.getCurrentSession().save(traderCategory);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(TraderCategory traderCategory) {
        log.debug("calling update(" + traderCategory + ")");
        sessionFactory.getCurrentSession().update(traderCategory);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(TraderCategory traderCategory) throws DSAValidationException{
        log.debug("calling remove(" + traderCategory + ")");

        List<Trader> traders = traderDao.getAllByCategory(traderCategory);
        if (traders.isEmpty()) {
            sessionFactory.getCurrentSession().delete(traderCategory);
        } else {
            throw new DSAValidationException("existing traders in this category");
        }
    }

    @Override
    public TraderCategory get(int id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(TraderCategory.class, id);

        if (result == null) {
            throw new DSARuntimeException("Leider existiert für diese ID keine Trader Category");
        }
        log.trace("returning " + result);
        return (TraderCategory) result;
    }

    @Override
    public List<TraderCategory> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("TraderCategory.findAll").list();

        List<TraderCategory> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((TraderCategory) o);
        }
        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
