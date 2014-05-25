package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.dao.CurrencyDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private CurrencyDao currencyDao;

    @Override
    public Currency get(int id) {
        log.debug("calling get(" + id + ")");
        Currency result = currencyDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public void add(Currency r) {
        log.debug("calling add(" + r + ")");
        validate(r);
        currencyDao.add(r);
    }

    @Override
    public void update(Currency r) {
        log.debug("calling update(" + r + ")");
        validate(r);
        currencyDao.update(r);
    }

    @Override
    public void remove(Currency r) {
        log.debug("calling remove(" + r + ")");
        currencyDao.remove(r);
    }

    @Override
    public List<Currency> getAll() {
        log.debug("calling getAll()");
        List<Currency> result = currencyDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public CurrencyAmount exchange(Currency from, Currency to) {
        return null;
    }

    public void setCurrencyDao(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public CurrencyDao getCurrencyDao() {
        return currencyDao;
    }

    /**
     * Validates a currency
     *
     * @param currency
     * @throws DSAValidationException if currency is not valid
     */
    private void validate(Currency currency) throws DSAValidationException {
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        if (violations.size() > 0) {
            throw new DSAValidationException("Währung ist nicht valide.", violations);
        }
    }
}
