package sepm.dsa.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class LocationConnectionDaoHbmImpl implements LocationConnectionDao {

    private static final Logger log = LoggerFactory.getLogger(LocationConnectionDaoHbmImpl.class);

    private SessionFactory sessionFactory;

    @Transactional(readOnly = false)
    @Override
    public void add(LocationConnection locationConnection) {
        log.debug("calling add(" + locationConnection + ")");
        sessionFactory.getCurrentSession().save(locationConnection);
        sessionFactory.getCurrentSession().flush();
    }

    @Transactional(readOnly = false)
    @Override
    public void update(LocationConnection locationConnection) {
        log.debug("calling update(" + locationConnection + ")");
        sessionFactory.getCurrentSession().merge(locationConnection);       // previously: update
        sessionFactory.getCurrentSession().flush();
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(LocationConnection locationConnection) {
        log.debug("calling delete(" + locationConnection + ")");
        sessionFactory.getCurrentSession().delete(locationConnection);
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public LocationConnection get(Location location1, Location location2) {
        log.debug("calling get(" + location1 + ", " + location2 + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("LocationConnection.findByLocations");
        query.setParameter("location1ID", location1.getId());
        query.setParameter("location2ID", location2.getId());
        List<?> list = query.list();

        List<LocationConnection> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((LocationConnection) o);
        }
        if (result.size() > 1) {
            log.warn("INCONSISTENT DATA! More than 1 connections between locations " + location1 + " and " + location2);
        }
        if (result.size() == 0) {
            log.trace("returning null");
            return null;
        }
        log.trace("returning " + result);
        return result.get(0);
    }

    @Override
    public List<LocationConnection> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("LocationConnection.findAll").list();

        List<LocationConnection> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((LocationConnection) o);
        }

	    log.trace("returning " + result);
        return result;
    }

    @Override
    public List<LocationConnection> getAllByLocationName(Location location, String locationName) {
        log.debug("calling getAllByLocationName(" + location + ", " + locationName + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("LocationConnection.findAllByFilter1");
        query.setParameter("locationId", location.getId());
        query.setParameter("locationName", locationName);
        List<?> list = query.list();
        List<LocationConnection> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((LocationConnection) o);
        }

	    log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
