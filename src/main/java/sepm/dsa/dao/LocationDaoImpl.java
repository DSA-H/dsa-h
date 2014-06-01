package sepm.dsa.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Location;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class LocationDaoImpl
	extends BaseDaoHbmImpl<Location>
	implements LocationDao {

    @Override
    public void remove(Location location) {
        super.remove(location);

        location.getAllConnections().forEach(conn -> {
            Location l = conn.getLocation1().equals(location) ? conn.getLocation2() : conn.getLocation1();
            l.removeConnection(conn);
        });
    }

    @Override
    public List<Location> getAllByRegion(int regionId) {
        log.debug("calling getAll()");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Location.findAllByRegion");
        query.setParameter("regionId", regionId);
        List<?> list = query.list();

        List<Location> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Location) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Location> getAllByNameNotConnectedTo(Location location, String locationName) {
        log.debug("calling getAllByNameNotConnectedTo(" + location + "," + locationName + ")");

        Query query = sessionFactory.getCurrentSession().getNamedQuery("Location.findAllByNameNotConnected");
        query.setParameter("ownLocationId", location.getId());
        query.setParameter("locationName", locationName);
        List<?> list = query.list();

        List<Location> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Location) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Location> getAllAround(Location location, double withinDistance) {
        log.debug("calling getAllAround(" + location + "," + withinDistance + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Location.findAllAround");
        query.setParameter("ownLocationId", location.getId());
        query.setParameter("xCoord", location.getxCoord());
        query.setParameter("yCoord", location.getyCoord());
        query.setParameter("distance", withinDistance);
        List<?> list = query.list();

        List<Location> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Location) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Location> getAllAroundNotConnected(Location location, double withinDistance) {
        log.debug("calling getAllAroundNotConnected(" + location + "," + withinDistance + ")");

        Query query = sessionFactory.getCurrentSession().getNamedQuery("Location.findAllAroundNotConnected");
        query.setParameter("ownLocationId", location.getId());
        query.setParameter("xCoord", location.getxCoord());
        query.setParameter("yCoord", location.getyCoord());
        query.setParameter("distance", withinDistance);
        List<?> list = query.list();

        List<Location> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Location) o);
        }

        log.trace("returning " + result);
        return result;
    }
}
