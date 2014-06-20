package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationConnectionDao;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.exceptions.DSAAlreadyExistsException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.path.NoPathException;
import sepm.dsa.service.path.PathService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class LocationConnectionServiceImpl implements LocationConnectionService {

    private static final Logger log = LoggerFactory.getLogger(LocationConnectionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

	private PathService pathService;

    private LocationConnectionDao locationConnectionDao;

    private LocationDao locationDao;

    public void setLocationConnectionDao(LocationConnectionDao locationConnectionDao) {
        log.debug("calling setLocationConnectionDao()");
        this.locationConnectionDao = locationConnectionDao;
    }

    @Transactional(readOnly = false)
    @Override
    public LocationConnection add(LocationConnection locationConnection) {
        log.debug("calling addConnection(" + locationConnection + ")");
        validate(locationConnection);
        if (get(locationConnection.getLocation1(), locationConnection.getLocation2()) != null) {
            throw new DSAAlreadyExistsException("Die Verbindung zwischen "
                    + locationConnection.getLocation1().getName() + " und "
                    + locationConnection.getLocation2().getName() + " exisitiert bereits.");
        }
        LocationConnection lc = locationConnectionDao.add(locationConnection);
        log.info("added " + lc);
	    return lc;
    }

    @Transactional(readOnly = false)
    @Override
    public LocationConnection update(LocationConnection locationConnection) {
        log.debug("calling update(" + locationConnection + ")");
        validate(locationConnection);
        LocationConnection trueConn = get(locationConnection.getLocation1(), locationConnection.getLocation2());
        if(trueConn != null) {
            locationConnection.setLocation1(trueConn.getLocation1());
            locationConnection.setLocation2(trueConn.getLocation2());
        }
        LocationConnection lc = locationConnectionDao.update(locationConnection);
        log.info("updated " + lc);
	    return lc;
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(LocationConnection locationConnection) {
        log.debug("calling removeConnection(" + locationConnection + ")");
        locationConnectionDao.remove(locationConnection);
        log.info("removed " + locationConnection);
    }

    @Override
    public LocationConnection get(Location location1, Location location2) {
        log.debug("calling get(" + location1 + ", " + location2 + ")");
        LocationConnection.Pk pk = new LocationConnection.Pk(location1, location2);
        return locationConnectionDao.get(pk);
    }

	@Override
	public List<LocationConnection> getAll() {
		log.debug("calling getAll()");
		List<LocationConnection> result = locationConnectionDao.getAll();
		return result;
	}

    @Override
    public List<LocationConnection> getShortestPathBetween(Location location1, Location location2) throws NoPathException{
        log.debug("calling getShortestPathBetween(" + location1 + ", " + location2 + ")");

	    List<Location> allLocations = locationDao.getAll();
	    List<LocationConnection> allConnections = locationConnectionDao.getAll();
	    List<Location> endLocation = new ArrayList<>();
	    endLocation.add(location2);
	    return pathService.findShortestPath(allLocations, allConnections, location1, endLocation);
    }

    @Override
    public List<LocationConnection> getAllByLocation(int locationId) {
        log.debug("calling getAllByLocation(" + locationId + ")");
        List<LocationConnection> result = locationConnectionDao.getAllByLocation(locationId);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<LocationConnection> suggestLocationConnectionsByFilter(Location location, String filter) {
        log.debug("calling suggestLocationConnectionsByFilter(" + location + "," + filter + ")");
        //extract filter info (currently just location name)
        String locationName = filter == null ? "%" : "%" + filter + "%";
        List<Location> locations = locationDao.getAllByNameWithoutLocation(location, locationName);
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
        List<Location> nearLocations = locationDao.getAllAround(location, withinDistance);
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

	public void setPathService(PathService pathService) {
		this.pathService = pathService;
	}

    /**
     * Validates a locationConnection
     *
     * @param locationConnection
     * @throws sepm.dsa.exceptions.DSAValidationException if region is not valid
     */
    public void validate(LocationConnection locationConnection) throws DSAValidationException {
        log.info("calling validate(" + locationConnection + ")");
        Set<ConstraintViolation<LocationConnection>> violations = validator.validate(locationConnection);
        if (violations.size() > 0) {
            throw new DSAValidationException("Gebiet ist nicht valide.", violations);
        }
    }
}
