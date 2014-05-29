package sepm.dsa.dao;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class LocationConnectionDaoHbmImpl
	extends BaseDaoHbmImpl<LocationConnection>
	implements LocationConnectionDao {

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
