package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.UnitTypeDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.UnitType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class UnitTypeServiceImpl implements UnitTypeService {

    private static final Logger log = LoggerFactory.getLogger(UnitTypeServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private UnitTypeDao unitTypeDao;

    @Override
    public UnitType get(Integer id) {
        log.debug("calling get(" + id + ")");
        UnitType result = unitTypeDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public int add(UnitType p) {
        log.debug("calling add(" + p + ")");
        validate(p);
        return unitTypeDao.add(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(UnitType p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        unitTypeDao.update(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(UnitType p) {
        log.debug("calling remove(" + p + ")");
        unitTypeDao.remove(get(p.getId()));
    }

    @Override
    public List<UnitType> getAll() {
        log.debug("calling getAll()");
        List<UnitType> result = unitTypeDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    /**
     * Validates a unitType
     *
     * @param unitType
     * @throws sepm.dsa.exceptions.DSAValidationException if unitType is not valid
     */
    private void validate(UnitType unitType) throws DSAValidationException {
        Set<ConstraintViolation<UnitType>> violations = validator.validate(unitType);
        if (violations.size() > 0) {
            throw new DSAValidationException("UnitType ist nicht valide.", violations);
        }
    }

    public void setUnitTypeDao(UnitTypeDao unitTypeDao) {
        log.debug("calling setProductDao(" + unitTypeDao + ")");
        this.unitTypeDao = unitTypeDao;
    }
}
