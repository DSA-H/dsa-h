package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.RegionBorderDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.RegionBorder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("RegionBorderService")
@Transactional(readOnly = true)
public class RegionBorderServiceImpl implements RegionBorderService {

    private static final Logger log = LoggerFactory.getLogger(RegionBorderServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private RegionBorderDao regionBorderDao;

    @Override
    @Transactional(readOnly = false)
    public void add(RegionBorder regionBorder) {
        log.debug("calling add(" + regionBorder + ")");
        validate(regionBorder);
        regionBorderDao.add(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(RegionBorder regionBorder) {
        log.debug("calling update(" + regionBorder + ")");
        validate(regionBorder);
        regionBorderDao.update(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(RegionBorder regionBorder) {
        log.debug("calling remove(" + regionBorder + ")");
        regionBorderDao.remove(regionBorder);
    }

    @Override
    public List<RegionBorder> getAll() {
        log.debug("calling getAll()");
        List<RegionBorder> result = regionBorderDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<RegionBorder> getAllByRegion(int regionId) throws DSARuntimeException {
        log.debug("calling getAll()");
        List<RegionBorder> result = new ArrayList<>();
        result = regionBorderDao.getAllByRegion(regionId);
        log.trace("returning " + result);
        return result;
    }

    public void setRegionBorderDao(RegionBorderDao regionBorderDao) {
        log.debug("calling setRegionBorderDao(" + regionBorderDao + ")");
        this.regionBorderDao = regionBorderDao;
    }

    /**
     * Validates a region border
     *
     * @param regionBorder
     * @throws sepm.dsa.exceptions.DSAValidationException if region border is not valid
     */
    private void validate(RegionBorder regionBorder) throws DSAValidationException {
//        TODO region1 must not equals region2
        log.debug("calling validate(" + regionBorder + ")");
        Set<ConstraintViolation<RegionBorder>> violations = validator.validate(regionBorder);
        if (violations.size() > 0) {
            throw new DSAValidationException("Gebietsgrenze ist nicht valide.", violations);
        }
    }


}