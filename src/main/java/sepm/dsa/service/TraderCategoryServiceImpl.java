package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.exceptions.DSAValidationException;
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
public class TraderCategoryServiceImpl implements TraderCategoryService, Serializable {
    private static final long serialVersionUID = -3272024123454792934L;
    private static final Logger log = LoggerFactory.getLogger(TraderCategoryServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TraderCategoryDao traderCategoryDao;

    @Override
    public TraderCategory get(int id) {
        log.debug("calling get(" + id + ")");
        TraderCategory result = traderCategoryDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void add(TraderCategory t) {
        log.debug("calling add(" + t + ")");
        validate(t);
        traderCategoryDao.add(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(TraderCategory t) {
        log.debug("calling update(" + t + ")");
        validate(t);
        traderCategoryDao.update(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(TraderCategory t) {
        log.debug("calling remove(" + t + ")");
        traderCategoryDao.remove(t);
    }

    @Override
    public List<TraderCategory> getAll() {
        log.debug("calling getAll()");
        List<TraderCategory> result = traderCategoryDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setTraderCategoryDao(TraderCategoryDao traderCategoryDao) {
        this.traderCategoryDao = traderCategoryDao;
    }

    /**
     * Validates a TraderCategory
     *
     * @param traderCategory must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if location is not valid
     */
    private void validate(TraderCategory traderCategory) throws DSAValidationException {
        Set<ConstraintViolation<TraderCategory>> violations = validator.validate(traderCategory);
        if (violations.size() > 0) {
            throw new DSAValidationException("Händlerkategorie ist nicht valide.", violations);
        }
    }
}
