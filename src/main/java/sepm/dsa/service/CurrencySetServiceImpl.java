package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.CurrencySetDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.CurrencySet;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class CurrencySetServiceImpl implements CurrencySetService {

    private static final Logger log = LoggerFactory.getLogger(CurrencySetServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private CurrencySetDao currencySetDao;

    @Override
    public CurrencySet get(int id) {
        log.debug("calling get(" + id + ")");
        CurrencySet result = currencySetDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public CurrencySet add(CurrencySet r) {
        log.debug("calling add(" + r + ")");
        validate(r);
        return currencySetDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public CurrencySet update(CurrencySet r) {
        log.debug("calling update(" + r + ")");
        return currencySetDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(CurrencySet r) {
        log.debug("calling removeConnection(" + r + ")");
        currencySetDao.remove(r);
    }

    @Override
    public List<CurrencySet> getAll() {
        log.debug("calling getAll()");
        List<CurrencySet> result = currencySetDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setCurrencySetDao(CurrencySetDao currencySetDao) {
        this.currencySetDao = currencySetDao;
    }

    /**
     * Validates a currencySet
     *
     * @param currencySet the currencySet to be validated
     * @throws sepm.dsa.exceptions.DSAValidationException if currencySet is not valid
     */
    private void validate(CurrencySet currencySet) throws DSAValidationException {
        Set<ConstraintViolation<CurrencySet>> violations = validator.validate(currencySet);
        if (violations.size() > 0) {
            throw new DSAValidationException("CurrencySet ist nicht valide.", violations);
        }
    }
    
}
