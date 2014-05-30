package sepm.dsa.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class LocationConnectionDaoHbmImpl
        extends BaseDaoHbmImpl<LocationConnection>
        implements LocationConnectionDao {

    private static final Logger log = LoggerFactory.getLogger(LocationConnectionDaoHbmImpl.class);

    @Transactional(readOnly = false)
    @Override
    public void remove(LocationConnection locationConnection) {
        log.debug("calling delete(" + locationConnection + ")");
        LocationConnection trueLocationConnection = get(locationConnection.getPk());    //
        sessionFactory.getCurrentSession().delete(trueLocationConnection);
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public LocationConnection get(Serializable id) {
        log.debug("calling get(" + id + ")");
        LocationConnection.Pk pk = (LocationConnection.Pk) id;
        Query query = sessionFactory.getCurrentSession().getNamedQuery("LocationConnection.findByLocations");
        query.setParameter("location1ID", pk.getLocation1().getId());
        query.setParameter("location2ID", pk.getLocation2().getId());
        List<?> list = query.list();

        List<LocationConnection> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((LocationConnection) o);
        }
        if (result.size() > 1) {
            log.warn("INCONSISTENT DATA! More than 1 connections between locations " + pk.getLocation1() + " and " + pk.getLocation2());
        }
        if (result.size() == 0) {
            log.trace("returning null");
            return null;
        }
        log.trace("returning " + result);
        return result.get(0);
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

}
