package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.DealDao;
import sepm.dsa.dao.PlayerDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Player;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class PlayerServiceImpl implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private DealDao dealDao;
    private PlayerDao playerDao;

    @Override
    @Transactional(readOnly = false)
    public void add(Player player) {

        log.debug("calling add(" + player + ")");
        validate(player);
        playerDao.add(player);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Player player) {
        log.debug("calling update(" + player + ")");
        validate(player);
        playerDao.update(player);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Player player) {
        log.debug("calling remove(" + player + ")");
        dealDao.getAllByPlayer(player).forEach(dealDao::remove);    // Cascade.REMOVE all deals
        playerDao.remove(player);
    }

    @Override
    public Player get(int id) {
        log.debug("calling get(" + id + ")");
        Player result = playerDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Player> getAll() {
        log.debug("calling getAll()");
        List<Player> result = playerDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    /**
     * Validates a Player
     *
     * @param player must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if player is not valid
     */
    private void validate(Player player) throws DSAValidationException {
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        if (violations.size() > 0) {
            throw new DSAValidationException("Gebiet ist nicht valide.", violations);
        }
    }

    public void setPlayerDao(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public void setDealDao(DealDao dealDao) {
        this.dealDao = dealDao;
    }
}
