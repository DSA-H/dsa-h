package sepm.dsa.service;

import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationConnectionDao;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dao.LocationDaoImpl;
import sepm.dsa.exceptions.DSAAlreadyExistsException;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Service("LocationConnectionService")
public class LocationConnectionServiceImpl implements LocationConnectionService {

    private static final Logger log = LoggerFactory.getLogger(LocationConnectionServiceImpl.class);

    private LocationConnectionDao locationConnectionDao;

    private LocationDao locationDao;

    public void setLocationConnectionDao(LocationConnectionDao locationConnectionDao) {
        log.debug("calling setLocationConnectionDao()");
        this.locationConnectionDao = locationConnectionDao;
    }

    @Transactional(readOnly = false)
    @Override
    public void add(LocationConnection locationConnection) {
        log.debug("calling add(" + locationConnection + ")");
        if (get(locationConnection.getLocation1(), locationConnection.getLocation2()) != null) {
            throw new DSAAlreadyExistsException();
        }
        locationConnectionDao.add(locationConnection);
        log.info("added " + locationConnection);
    }

    @Transactional(readOnly = false)
    @Override
    public void update(LocationConnection locationConnection) {
        log.debug("calling update(" + locationConnection + ")");
        LocationConnection trueConn = get(locationConnection.getLocation1(), locationConnection.getLocation2());
        locationConnection.setLocation1(trueConn.getLocation1());
        locationConnection.setLocation2(trueConn.getLocation2());
        locationConnectionDao.update(locationConnection);
        log.info("updated " + locationConnection);
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(LocationConnection locationConnection) {
        log.debug("calling remove(" + locationConnection + ")");
        LocationConnection trueConn = locationConnectionDao.get(locationConnection.getLocation1(), locationConnection.getLocation2());
        if (trueConn != null) {
            log.debug(" really remove " + trueConn);
            locationConnectionDao.remove(trueConn);
        }
        log.info("removed " + trueConn);
    }

    @Override
    public LocationConnection get(Location location1, Location location2) {
        log.debug("calling get(" + location1 + ", " + location2 + ")");
        return locationConnectionDao.get(location1, location2);
    }

    @Override
    public List<LocationConnection> getShortestPathBetween(Location location1, Location location2) {
        log.debug("calling getShortestPathBetween(" + location1 + ", " + location2 + ")");

        throw new DSARuntimeException("Not yet implemented");
    }

    @Override
    public List<LocationConnection> suggestLocationConnectionsByFilter(Location location, String filter) {
        log.debug("calling suggestLocationConnectionsByFilter(" + location + "," + filter + ")");
        //extract filter info (currently just location name)
        String locationName = filter == null ? null : "%" + filter + "%";
        List<Location> locations = locationDao.getAllByNameNotConnectedTo(location, locationName);
        List<LocationConnection> result = makeconnectionSuggestions(location, locations);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<LocationConnection> getAllByLocationFilter1(Location location, String filter) {
        log.debug("calling getAllByLocationFilter1(" + location + "," + filter + ")");
        //extract filter info (currently just location name)
        String locationName = filter == null ? null : "%" + filter + "%";
        List<LocationConnection> result = locationConnectionDao.getAllByLocationName(location, locationName);
        log.trace("returning " + result);
        return result;
    }

    private List<LocationConnection> makeconnectionSuggestions(Location from, List<Location> to) {
        List<LocationConnection> result = new ArrayList<>(to.size());
        for (Location l : to) {
            LocationConnection suggestion = new LocationConnection();
            suggestion.setLocation1(from);
            suggestion.setLocation2(l);
            double distanceSuggested = suggestedDistanceBetween(from, l);
            int suggestedTravelTime = suggestedTravelTimeForDistance(distanceSuggested);
            suggestion.setTravelTime(suggestedTravelTime);
            result.add(suggestion);
        }
        return result;
    }


    @Override
    public List<LocationConnection> suggestLocationConnectionsAround(Location location, double withinDistance) {
        log.debug("calling suggestLocationConnectionsAround(" + location + "," + withinDistance + ")");
        List<Location> nearLocations = locationDao.getAllAroundNotConnected(location, withinDistance);
        List<LocationConnection> result = makeconnectionSuggestions(location, nearLocations);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public double suggestedDistanceBetween(Location location1, Location location2) {
        log.debug("calling suggestedDistanceBetween(" + location1 + "," + location2 + ")");
        double result = Math.sqrt(Math.pow(location1.getxCoord() - location2.getxCoord(), 2) + Math.pow(location1.getyCoord() - location2.getyCoord(), 2));
        log.trace("returning " + result);
        return result;
    }

    @Override
    public int suggestedTravelTimeForDistance(double distance) {
        log.debug("calling suggestedTravelTimeForDistance(" + distance + ")");

        int result = (int) (distance / 10); // TODO find good value
        log.trace("returning " + result);
        return result;
    }

    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }
}
