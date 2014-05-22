package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSAModelNotFoundException;
import sepm.dsa.model.Player;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class PlayerDaoImpl implements PlayerDao {
	private SessionFactory sessionFactory;
	private static final Logger log = LoggerFactory.getLogger(LocationDaoImpl.class);

	@Override
	@Transactional(readOnly = false)
	public void add(Player player) {
		log.debug("calling add(" + player + ")");
		sessionFactory.getCurrentSession().save(player);
	}

	@Override
	public void update(Player player) {
		log.debug("calling update(" + player + ")");
		sessionFactory.getCurrentSession().update(player);
	}

	@Override
	public void remove(Player player) {
		log.debug("calling remove(" + player + ")");
		sessionFactory.getCurrentSession().delete(player);
	}

	@Override
	public Player get(int id) throws DSAModelNotFoundException {
		log.debug("calling get("+id+")");
		return (Player) sessionFactory.getCurrentSession().get(Player.class, id);
	}

	@Override
	public List<Player> getAll() {
		log.debug("calling getAll()");
		List<Player> players = new ArrayList<>();

		sessionFactory.getCurrentSession()
			.getNamedQuery("Player.findAll")
			.list()
			.forEach(o -> players.add((Player) o));

		log.trace("returning " + players);
		return players;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
