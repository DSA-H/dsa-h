package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARegionNotExistingException;
import sepm.dsa.model.Location;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class LocationDaoImpl implements LocationDao {

    private static final Logger log = LoggerFactory.getLogger(LocationDaoImpl.class);
    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(Location location) {
        log.debug("calling add(" + location + ")");
        sessionFactory.getCurrentSession().save(location);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Location location) {
        log.debug("calling update(" + location + ")");
        sessionFactory.getCurrentSession().update(location);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Location location) {

        log.debug("calling remove(" + location + ")");
        sessionFactory.getCurrentSession().delete(location);
    }

    @Override
    public Location get(int id){
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(Location.class, id);

        if (result == null) {
            log.trace("returning " + result);
            return null;
        }
        log.trace("returning " + result);
        return (Location) result;
    }

    @Override
    public List<Location> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("Location.findAll").list();

        List<Location> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Location) o);
        }

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}