package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.dao.CurrencySetDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.model.Region;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class CurrencySetServiceImpl implements CurrencySetService {

    private static final Logger log = LoggerFactory.getLogger(CurrencySetServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private CurrencySetDao currencySetDao;
    private CurrencyService currencyService;
    private RegionService regionService;

    private final static List<Integer> defaultCurrencySets = Arrays.asList(1);   // IDs

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
        validate(r);
        return currencySetDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(CurrencySet r) {
        log.debug("calling removeConnection(" + r + ")");
        if (defaultCurrencySets.contains(r.getId())) {
            throw new DSAValidationException("Standardwährungssysteme dürfen nicht gelöscht werden!");
        }
        List<Region> regions = regionService.getAllByPreferredCurrencySet(r);
        currencySetDao.remove(r);
        for (Region region : regions) {
            region.setPreferredCurrencySet(null);
        }
    }

    @Override
    public List<CurrencySet> getAll() {
        log.debug("calling getAll()");
        List<CurrencySet> result = currencySetDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public CurrencySet getDefaultCurrencySet() {
        log.debug("calling getDefaultCurrencySet()");
        CurrencySet result = currencySetDao.get(1);
        if (result == null) {
            throw new DSARuntimeException("Keine Standardwährung gespeichert, importieren Sie bitte einen geeigneten Datensatz!");
        }
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<CurrencyAmount> toCurrencySet(CurrencySet currencySet, final Integer baseRateAmount) {
        log.debug("calling toCurrencySet(" + currencySet + ", " + currencySet + ")");

        Integer remaingAmount = baseRateAmount;
        List<Currency> currencies = currencyService.getAllByCurrencySet(currencySet);
        List<CurrencyAmount> result = new ArrayList<>(currencies.size()); 
        for (int i=0; i<currencies.size(); i++) {
            Currency c = currencies.get(i);
            CurrencyAmount a = new CurrencyAmount();
            a.setCurrency(c);
            a.setAmount(remaingAmount / c.getValueToBaseRate());
            if (a.getAmount() > 0) {
                remaingAmount = remaingAmount % c.getValueToBaseRate();
            }
            result.add(a);
        }
        log.trace("returning " + result);
        return result;
    }

    public void setCurrencySetDao(CurrencySetDao currencySetDao) {
        log.debug("calling setCurrencySetDao(" + currencySetDao + ")");
        this.currencySetDao = currencySetDao;
    }

    /**
     * Validates a currencySet
     *
     * @param currencySet the currencySet to be validated
     * @throws sepm.dsa.exceptions.DSAValidationException if currencySet is not valid
     */
    private void validate(CurrencySet currencySet) throws DSAValidationException {
        log.debug("calling validate(" + currencySet + ")");
        Set<ConstraintViolation<CurrencySet>> violations = validator.validate(currencySet);
        if (violations.size() > 0) {
            throw new DSAValidationException("CurrencySet ist nicht valide.", violations);
        }
    }

    public void setCurrencyService(CurrencyService currencyService) {
        log.debug("calling setCurrencyService(" + currencyService + ")");
        this.currencyService = currencyService;
    }

    public void setRegionService(RegionService regionService) {
        log.debug("calling setRegionService(" + regionService + ")");
        this.regionService = regionService;
    }
}
