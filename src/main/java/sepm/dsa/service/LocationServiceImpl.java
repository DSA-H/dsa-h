package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("LocationService")
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService, Serializable {

    private static final long serialVersionUID = -3272024118547942934L;
    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private LocationDao locationDao;

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
    @Transactional(readOnly = false)
    public void remove(Location location) {
        log.debug("calling remove(" + location + ")");
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
            throw new DSAValidationException("Gebiet ist nicht valide.", violations);
        }
    }
}
