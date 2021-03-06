package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.DealDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class DealServiceImpl implements DealService {

    private static final Logger log = LoggerFactory.getLogger(DealServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TimeService timeService;

    private DealDao dealDao;

    @Override
    public Deal get(int id) {
        log.debug("calling get(" + id + ")");
        Deal result = dealDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Deal add(Deal r) {
        log.debug("calling add(" + r + ")");
        validate(r);
        return dealDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public Deal update(Deal r) {
        log.debug("calling update(" + r + ")");
        validate(r);
        return dealDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Deal r) {
        log.debug("calling removeConnection(" + r + ")");
        dealDao.remove(r);
    }

    @Override
    public List<Deal> getAll() {
        log.debug("calling getAll()");
        List<Deal> result = dealDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Deal> getAllBetweenPlayerAndTraderLastXDays(Player player, Trader trader, long days) {
        log.debug("calling getAllBetweenPlayerAndTraderLastXDays(" + player + ", " + trader + ", " + days + ")");
        long currentDateValue = timeService.getCurrentDate().getTimestamp();
        List<Deal> result = dealDao.playerDealsWithTraderInTimeRange(player, trader, currentDateValue - days, currentDateValue);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Deal> getAllByPlayer(Player player) {
        log.debug("calling getAllByPlayer(" + player + ")");
        List<Deal> result = dealDao.getAllByPlayer(player);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Deal> getAllByProduct(Product product) {
        log.debug("calling getAllByProduct(" + product + ")");
        List<Deal> result = dealDao.getAllByProduct(product);
        log.trace("returning " + result);
        return result;
    }

    /**
     * Validates a deal
     *
     * @param deal the deal to be validated
     * @throws sepm.dsa.exceptions.DSAValidationException if deal is not valid
     */
    public void validate(Deal deal) throws DSAValidationException {
        log.debug("calling validate(" + deal + ")");
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        if (violations.size() > 0) {
            throw new DSAValidationException("Deal ist nicht valide.", violations);
        }
    }

    public void setDealDao(DealDao dealDao) {
        log.debug("calling setDealDao(" + dealDao + ")");
        this.dealDao = dealDao;
    }

    public void setTimeService(TimeService timeService) {
        log.debug("calling setTimeService(" + timeService + ")");
        this.timeService = timeService;
    }
}
