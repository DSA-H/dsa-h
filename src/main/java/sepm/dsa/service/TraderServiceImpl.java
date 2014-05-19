package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by Jotschi on 19.05.2014.
 */
public class TraderServiceImpl implements TraderService, Serializable {
    private static final long serialVersionUID = -3972024123454778934L;
    private static final Logger log = LoggerFactory.getLogger(TraderServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TraderDao traderDao;

    @Override
    public Trader get(int id) {
        log.debug("calling get(" + id + ")");
        Trader result = traderDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void add(Trader t) {
        log.debug("calling add(" + t + ")");
        validate(t);
        traderDao.add(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Trader t) {
        log.debug("calling update(" + t + ")");
        validate(t);
        traderDao.update(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Trader t) {
        log.debug("calling remove(" + t + ")");
        traderDao.remove(t);
    }

    @Override
    public List<Trader> getAllForLocation(Location location) {
        log.debug("calling getAll()");
        List<Trader> result = traderDao.getAllByLocation(location);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Trader> getAllByCategory(TraderCategory traderCategory) {
        log.debug("calling getAll()");
        List<Trader> result = traderDao.getAllByCategory(traderCategory);
        log.trace("returning " + result);
        return result;
    }

    public void setTraderDao(TraderDao traderDao) {
        this.traderDao = traderDao;
    }

    /**
     * Validates a Trader
     *
     * @param trader must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if location is not valid
     */
    private void validate(Trader trader) throws DSAValidationException {
        Set<ConstraintViolation<Trader>> violations = validator.validate(trader);
        if (violations.size() > 0) {
            throw new DSAValidationException("Händler ist nicht valide.", violations);
        }
    }
}
