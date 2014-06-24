package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.TavernDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Tavern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service("tavernService")
@Transactional(readOnly = true)
public class TavernServiceImpl implements TavernService {

    private static final Logger log = LoggerFactory.getLogger(TavernServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private TavernDao tavernDao;

    @Override
    @Transactional(readOnly = false)
    public Tavern add(Tavern tavern) {
        log.debug("calling add(" + tavern + ")");
        validate(tavern);
        return tavernDao.add(tavern);
    }

    @Override
    @Transactional(readOnly = false)
    public Tavern update(Tavern tavern) {
        log.debug("calling update(" + tavern + ")");
        validate(tavern);
        return tavernDao.update(tavern);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Tavern tavern) {
        log.debug("calling removeConnection(" + tavern + ")");
        tavernDao.remove(tavern);
    }

    @Override
    public Tavern get(int id) {
        log.debug("calling get(" + id + ")");
        Tavern result = tavernDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Tavern> getAll() {
        log.debug("calling getAll()");
        List<Tavern> result = tavernDao.getAll();
        log.trace("returning " + result);
        return result;
    }


    @Override
    public int calculatePrice(Tavern tavern) {
        log.debug("calling calculatePrice(" + tavern + ")");
        double result = 0;
        switch (tavern.getQuality()) {
            case MIES:
                result = 10 * (1 + (
                        (-5) * tavern.getLocation().getSize().getValue()
                        + (int) Math.ceil(Math.random() * 20) - (int) Math.ceil(Math.random() * 20)
                        + 5 * (tavern.getUsage()/tavern.getBeds())
                        ) / 100);
                return (int)result;
            case MANGELHAFT:
                result = 50 * (1 + (
                        (-1.25) * tavern.getLocation().getSize().getValue()
                        + (int) Math.ceil(Math.random() * 20) - (int) Math.ceil(Math.random() * 20)
                        + 10 * (tavern.getUsage()/tavern.getBeds())
                          ) / 100);
                return (int)result;
            case NORMAL:
                result = 100 * (1 + (
                        + (int) Math.ceil(Math.random() * 20) - (int) Math.ceil(Math.random() * 20)
                        + 20 * (tavern.getUsage()/tavern.getBeds())
                        ) / 100);
                return (int)result;
            case HERAUSRAGEND:
                result = 500 * (1 + (
                        5 * tavern.getLocation().getSize().getValue()
                        + (int) Math.ceil(Math.random() * 20) - (int) Math.ceil(Math.random() * 20)
                        + Math.floor(0.1+tavern.getUsage()/(float)tavern.getBeds())
                        + 20*tavern.getUsage()/tavern.getBeds()
                        ) / 100);
                return (int)result;
            case AUSSERGEWOEHNLICH:
                result = 1000 * (1 + (
                        12.5 * tavern.getLocation().getSize().getValue()
                        + (int) Math.ceil(Math.random() * 20) - (int) Math.ceil(Math.random() * 20)
                        + (0.2+tavern.getUsage()/(float)tavern.getBeds())
                        + 2*(0.1+tavern.getUsage()/(float)tavern.getBeds())
                        + 20*tavern.getUsage()/tavern.getBeds()
                        ) / 100);
                return (int)result;
            default:
                // should not happen
                return 0;
        }
    }

    @Override
    public List<Tavern> getAllByLocation(int locationId) {
        log.debug("calling getAllByLocation(" + locationId + ")");
        List<Tavern> result = tavernDao.getAllByLocation(locationId);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public int calculateBedsUseage(Tavern tavern) {
        log.debug("calling getBedsUsage(" + tavern + ")");
        Random random = new Random();
        double gaus = random.nextGaussian() / 4f;
        gaus += 0.5f;
        int result = (int) Math.round(tavern.getBeds() * gaus);
        if (result > tavern.getBeds()) {
            result = tavern.getBeds();
        }
        if (result < 0) {
            result = 0;
        }
        log.trace("returning " + result);
        return result;
    }

    /**
     * Validates a Tavern
     *
     * @param tavern must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if tavern is not valid
     */
    private void validate(Tavern tavern) throws DSAValidationException {
        log.debug("calling validate(" + tavern + ")");
        Set<ConstraintViolation<Tavern>> violations = validator.validate(tavern);
        if (violations.size() > 0) {
            throw new DSAValidationException("Wirtshaus ist nicht valide.", violations);
        }
    }

    public void setTavernDao(TavernDao tavernDao) {
        log.debug("calling setTavernDao(" + tavernDao + ")");
        this.tavernDao = tavernDao;
    }
}
