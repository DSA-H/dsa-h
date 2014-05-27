package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.exceptions.DSAModelNotFoundException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Location;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service("locationService")
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private LocationDao locationDao;
    private TraderService traderService;

    @Override
    @Transactional(readOnly = false)
    public void add(Location location) {
        log.debug("calling add(" + location + ")");
        validate(location);
        locationDao.add(location);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Location location) {
        log.debug("calling update(" + location + ")");
        validate(location);
        locationDao.update(location);
    }

    @Override
    public List<Location> getAllByRegion(int regionId) {
        log.debug("calling getAllByRegion(" + regionId + ")");
        List<Location> result = locationDao.getAllByRegion(regionId);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Location location) {
        log.debug("calling remove(" + location + ")");
        if (location.getId() == null) {
            throw new DSAValidationException("Ort muss eine ID haben");
        }
        locationDao.remove(location);
    }

    @Override
    public Location get(int id) {
        log.debug("calling get(" + id + ")");
        Location result = locationDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Location> getAll() {
        log.debug("calling getAll()");
        List<Location> result = locationDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    /**
     * Validates a Location
     *
     * @param location must not be null
     * @throws DSAValidationException if location is not valid
     */
    private void validate(Location location) throws DSAValidationException {
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        if (violations.size() > 0) {
            throw new DSAValidationException("Ort ist nicht valide.", violations);
        }
    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }
}
