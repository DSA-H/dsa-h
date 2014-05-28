package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.UnitAmount;
import sepm.dsa.dao.UnitDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Unit;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class UnitServiceImpl implements UnitService {
    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private UnitDao productUnitDao;

    @Override
    public Unit get(Integer id) {
        log.debug("calling get(" + id + ")");
        Unit result = productUnitDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public int add(Unit p) {
        log.debug("calling add(" + p + ")");
        validate(p);
        return productUnitDao.add(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Unit p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        productUnitDao.update(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Unit p) {
        log.debug("calling remove(" + p + ")");
        productUnitDao.remove(get(p.getId()));
    }

    @Override
    public List<Unit> getAll() {
        log.debug("calling getAll()");
        List<Unit> result = productUnitDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public UnitAmount exchange(Unit from, Unit to, Double amount) {
        UnitAmount result = new UnitAmount();
        result.setAmount(amount * to.getValueToBaseUnit() / from.getValueToBaseUnit());
        result.setUnit(to);

        //TODO do in model --> means do in dao and model
        return result;
    }

    public void setProductDao(UnitDao productUnitDao) {
        log.debug("calling setProductDao(" + productUnitDao + ")");
        this.productUnitDao = productUnitDao;
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

    public UnitDao getProductUnitDao() {
        return productUnitDao;
    }

    public void setProductUnitDao(UnitDao productUnitDao) {
        this.productUnitDao = productUnitDao;
    }
}
