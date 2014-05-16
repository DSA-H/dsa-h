package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.RegionBorderDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.RegionBorderPk;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 11.05.2014.
 */
@Service("RegionBorderService")
@Transactional(readOnly = true)
public class RegionBorderServiceImpl implements RegionBorderService, Serializable {

    private static final long serialVersionUID = 7415861483489569621L;

//    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private RegionBorderDao regionBorderDao;

    @Override
    @Transactional(readOnly = false)
    public RegionBorderPk add(RegionBorder regionBorder) {
        validate(regionBorder);
        return regionBorderDao.add(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(RegionBorder regionBorder) {
        validate(regionBorder);
        regionBorderDao.update(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(RegionBorder regionBorder) {
        regionBorderDao.remove(regionBorder);
    }

    @Override
    public RegionBorder get(RegionBorderPk pk) {
        return regionBorderDao.get(pk);
    }

    @Override
    public List<RegionBorder> getAll() {
        return regionBorderDao.getAll();
    }

    @Override
    public List<RegionBorder> getAllByRegion(int regionId) {
        return regionBorderDao.getAllByRegion(regionId);
    }

    public void setRegionBorderDao(RegionBorderDao regionBorderDao) {
        this.regionBorderDao = regionBorderDao;
    }

    /**
     * Validates a region border
     * @param regionBorder
     * @throws sepm.dsa.exceptions.DSAValidationException if region border is not valid
     */
    private void validate(RegionBorder regionBorder) throws DSAValidationException {
        Set<ConstraintViolation<RegionBorder>> violations = validator.validate(regionBorder);
        if (violations.size() > 0) {
            throw new DSAValidationException("Gebietsgrenze ist nicht valide.", violations);
        }
    }


}
