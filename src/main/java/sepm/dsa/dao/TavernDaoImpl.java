package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSAModelNotFoundException;
import sepm.dsa.model.Tavern;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class TavernDaoImpl implements TavernDao {
	private SessionFactory sessionFactory;
	private static final Logger log = LoggerFactory.getLogger(LocationDaoImpl.class);

	@Override
	@Transactional(readOnly = false)
	public void add(Tavern tavern) {
		log.debug("calling add(" + tavern + ")");
		sessionFactory.getCurrentSession().save(tavern);
	}

	@Override
	public void update(Tavern tavern) {
		log.debug("calling update(" + tavern + ")");
		sessionFactory.getCurrentSession().update(tavern);
	}

	@Override
	public void remove(Tavern tavern) {
		log.debug("calling remove(" + tavern + ")");
		sessionFactory.getCurrentSession().delete(tavern);
	}

	@Override
	public Tavern get(int id) {
		log.debug("calling get(" + id + ")");
		Object o = sessionFactory.getCurrentSession().get(Tavern.class, id);
		if (o == null) {
			return null;
		}
		return (Tavern) o;
	}

	@Override
	public List<Tavern> getAll() {
		log.debug("calling getAll()");
		List<Tavern> taverns = new ArrayList<>();

		sessionFactory.getCurrentSession()
			.getNamedQuery("Tavern.findAll")
			.list()
			.forEach(o -> taverns.add((Tavern) o));

		log.trace("returning " + taverns);
		return taverns;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
