package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Unit;

import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class UnitDaoImpl implements UnitDao {

    private static final Logger log = LoggerFactory.getLogger(RegionDaoHbmImpl.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public int add(Unit Unit) {
        log.debug("calling add(" + Unit + ")");
        sessionFactory.getCurrentSession().save(Unit);
        return Unit.getId();
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Unit Unit) {
        log.debug("calling update(" + Unit + ")");
        sessionFactory.getCurrentSession().update(Unit);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Unit Unit) {
        log.debug("calling remove(" + Unit + ")");
        sessionFactory.getCurrentSession().delete(Unit);
    }

    @Override
    public Unit get(Integer id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(Unit.class, id);

        if (result == null) {
            return null;
        }
        log.trace("returning " + result);
        return (Unit) result;
    }

    @Override
    public List<Unit> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("ProductUnit.findAll").list();

        List<Unit> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Unit) o);
        }

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
