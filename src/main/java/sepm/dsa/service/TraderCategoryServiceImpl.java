package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class TraderCategoryServiceImpl implements TraderCategoryService {
    private static final Logger log = LoggerFactory.getLogger(TraderCategoryServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TraderCategoryDao traderCategoryDao;
	private TraderService traderService;

	@Override
    public TraderCategory get(int id) {
        log.debug("calling get(" + id + ")");
        TraderCategory result = traderCategoryDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public TraderCategory add(TraderCategory t) {
        log.debug("calling addConnection(" + t + ")");
        validate(t);
        return traderCategoryDao.add(t);
    }

    @Override
    @Transactional(readOnly = false)
    public TraderCategory update(TraderCategory t) {
        log.debug("calling update(" + t + ")");
        validate(t);
        return traderCategoryDao.update(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(TraderCategory t) {
        log.debug("calling remove(" + t + ")");

	    List<Trader> traders = traderService.getAllByCategory(t);
	    if (traders.isEmpty()) {
		    traderCategoryDao.remove(t);
	    } else {
            String msg = "Löschen nicht möglich. Zu dieser Kategorie sind noch folgende Händler vorhanden:";
            for (Trader tr : traders) {
                msg += "\n" + tr + " (in " + tr.getLocation().getName() + ")";
            }
		    throw new DSAValidationException(msg);
	    }
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

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}
}
