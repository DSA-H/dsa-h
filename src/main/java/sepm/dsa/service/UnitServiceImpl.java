package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.UnitAmount;
import sepm.dsa.dao.UnitDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class UnitServiceImpl implements UnitService {
    private static final Logger log = LoggerFactory.getLogger(UnitServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private UnitDao unitDao;

    @Override
    public Unit get(Integer id) {
        log.debug("calling get(" + id + ")");
        Unit result = unitDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Unit add(Unit p) {
        log.debug("calling add(" + p + ")");
        validate(p);
	    return unitDao.add(p);
    }

    @Override
    public void update(Unit p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        unitDao.update(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Unit p) {
        log.debug("calling remove(" + p + ")");
        unitDao.remove(get(p.getId()));
    }

    @Override
    public List<Unit> getAll() {
        log.debug("calling getAll()");
        List<Unit> result = unitDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public UnitAmount exchange(Unit from, Unit to, Double amount) {
        UnitAmount result = new UnitAmount();
        result.setAmount(from.exchange(amount, to));
        result.setUnit(to);

        return result;
    }

    @Override
    public List<Unit> getAllByType(UnitType unitType) {
        log.debug("calling getAll()");
        List<Unit> result = unitDao.getAllByType(unitType);
        log.trace("returning " + result);
        return result;
    }

    /**
     * Validates a product
     *
     * @param product
     * @throws sepm.dsa.exceptions.DSAValidationException if product is not valid
     */
    private void validate(Unit product) throws DSAValidationException {
        Set<ConstraintViolation<Unit>> violations = validator.validate(product);
        if (violations.size() > 0) {
            throw new DSAValidationException("Produktunit ist nicht valide.", violations);
        }
    }

    public void setUnitDao(UnitDao unitDao) {
        log.debug("calling setUnitTypeDao(" + unitDao + ")");
        this.unitDao = unitDao;
    }
}
