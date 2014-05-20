package sepm.dsa.service;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationConnectionDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import javax.naming.OperationNotSupportedException;
import java.util.List;

@Transactional(readOnly = true)
@Service("LocationConnectionService")
public class LocationConnectionServiceImpl implements LocationConnectionService {

    private LocationConnectionDao locationConnectionDao;

    public void setLocationConnectionDao(LocationConnectionDao locationConnectionDao) {
        this.locationConnectionDao = locationConnectionDao;
    }

    @Transactional(readOnly = false)
    @Override
    public void add(LocationConnection locationConnection) {
        locationConnectionDao.add(locationConnection);
    }

    @Transactional(readOnly = false)
    @Override
    public void update(LocationConnection locationConnection) {
        locationConnectionDao.update(locationConnection);
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(LocationConnection locationConnection) {
        locationConnectionDao.remove(locationConnection);
    }

    @Override
    public LocationConnection get(Location location1, Location location2) {
        return locationConnectionDao.get(location1, location2);
    }

    @Override
    public List<LocationConnection> getShortestPathBetween(Location location1, Location location2) {
        throw new DSARuntimeException("Not yet implemented");
    }
}
