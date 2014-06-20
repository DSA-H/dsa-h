package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.dao.CurrencyDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Transactional(readOnly = true)
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private CurrencyDao currencyDao;

    private final static List<Integer> defaultCurrencies = Arrays.asList(1, 2, 3, 4);   // IDs

    @Override
    public Currency get(int id) {
        log.debug("calling get(" + id + ")");
        Currency result = currencyDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Currency add(Currency r) {
        log.debug("calling addConnection(" + r + ")");
        validate(r);
        return currencyDao.add(r);
    }

    @Override
    @Transactional(readOnly = false)
    public Currency update(Currency r) {
        log.debug("calling update(" + r + ")");
        validate(r);
        return currencyDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Currency r) {
        log.debug("calling removeConnection(" + r + ")");

        if (defaultCurrencies.contains(r.getId())) {
            throw new DSAValidationException("Standardwährungen dürfen nicht gelöscht werden!");
        }
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
    public Integer exchangeToBaseRate(Currency from, Integer amount) {
        log.debug("calling exchangeToBaseRate(" + from + "," + amount + ")");
        Integer result = (int) (((double) amount) * from.getValueToBaseRate() + 0.5); //,4, RoundingMode.HALF_UP);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Currency> getAllByCurrencySet(CurrencySet currencySet) {
        log.debug("calling getAllByCurrencySet(" + currencySet + ")");
        List<Currency> result = currencyDao.getAllByCurrencySet(currencySet);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public Integer exchangeToBaseRate(List<CurrencyAmount> currencyAmounts) {
        int result = 0;
        for (CurrencyAmount a : currencyAmounts) {
            result += exchangeToBaseRate(a.getCurrency(), a.getAmount());
        }
        return result;
    }

    @Override
    public CurrencyAmount exchange(Currency from, Currency to, Integer amount) {
        CurrencyAmount result = new CurrencyAmount();
        result.setAmount((int) ((((double) amount) * from.getValueToBaseRate()) / (to.getValueToBaseRate()) + 0.5)); //,4, RoundingMode.HALF_UP));
        result.setCurrency(to);
        return result;
    }

    public void setCurrencyDao(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    /**
     * Validates a currency
     *
     * @param currency the currency to be validated
     * @throws DSAValidationException if currency is not valid
     */
    private void validate(Currency currency) throws DSAValidationException {
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        if (violations.size() > 0) {
            throw new DSAValidationException("Währung ist nicht valide.", violations);
        }
    }
}
