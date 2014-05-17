package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.model.Location;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.List;

@Service("LocationService")
@Transactional(readOnly = true)
public class LocationServiceImpl implements  LocationService, Serializable {

    private static final long serialVersionUID = -3272024118547942934L;
    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private LocationDao locationDao;
    @Override
    public void add(Location location) {
        log.debug("calling add(" + location + ")");
        validator(location);
        locationDao.add(location);
    }

    @Override
    public void update(Location location) {
        log.debug("calling update(" + location + ")");
        validator(location);
        locationDao.update(location);
    }

    @Override
    public void remove(Location location) {
        log.debug("calling remove(" + location + ")");
        locationDao.remove(get(location.getId()));
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
}
