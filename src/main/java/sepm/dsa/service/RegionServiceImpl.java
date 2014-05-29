package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.RegionBorderDao;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private RegionDao regionDao;
    private RegionBorderDao regionBorderDao;
    private LocationService locationService;

    @Override
    public Region get(int id) {
        log.info("calling get(" + id + ")");
        Region result = regionDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Region add(Region r) {
        log.info("calling addConnection(" + r + ")");
        validate(r);
        return regionDao.add(r);
    }

    @Override
    @Transactional(readOnly = false)
    public Region update(Region r) {
        log.info("calling update(" + r + ")");
        validate(r);
        return regionDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Region r) {
        log.info("calling removeConnection(" + r + ")");
//        List<RegionBorder> borders = regionBorderDao.getAllByRegion(r.getId());
//        List<Location> locations = locationService.getAllByRegion(r.getId());

//        borders.forEach(regionBorderDao::removeConnection);
//        locations.forEach(locationService::removeConnection);

        regionDao.remove(r);
    }

    @Override
    public List<Region> getAll() {
        log.info("calling getAll()");
        List<Region> result = regionDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setRegionDao(RegionDao regionDao) {
        log.debug("calling setRegionDao(" + regionDao + ")");
        this.regionDao = regionDao;
    }

    public void setRegionBorderDao(RegionBorderDao regionBorderDao) {
        log.debug("calling setRegionBorderDao(" + regionBorderDao + ")");
        this.regionBorderDao = regionBorderDao;
    }

    /**
     * Validates a region
     *
     * @param region
     * @throws DSAValidationException if region is not valid
     */
    private void validate(Region region) throws DSAValidationException {
        log.info("calling validate(" + region + ")");
        Set<ConstraintViolation<Region>> violations = validator.validate(region);
        if (violations.size() > 0) {
            throw new DSAValidationException("Gebiet ist nicht valide.", violations);
        }
    }


    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
