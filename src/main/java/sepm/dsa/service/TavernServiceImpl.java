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
		// @TODO Really calculate a price (Laurids liefert diese Woche noch konkrete Formel)
		return 4; // chosen by fair dice roll.
		// guaranteed to be random.
		// http://xkcd.com/221/
		// lol
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
		log.debug("calling getBedsUseage(" + tavern + ")");
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
		return result;
	}

	/**
	 * Validates a Tavern
	 *
	 * @param tavern must not be null
	 * @throws sepm.dsa.exceptions.DSAValidationException if tavern is not valid
	 */
	private void validate(Tavern tavern) throws DSAValidationException {
		Set<ConstraintViolation<Tavern>> violations = validator.validate(tavern);
		if (violations.size() > 0) {
			throw new DSAValidationException("Wirtshaus ist nicht valide.", violations);
		}
	}

	public void setTavernDao(TavernDao tavernDao) {
		this.tavernDao = tavernDao;
	}
}
