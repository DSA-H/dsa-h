package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class MovingTraderDaoHbmImpl
		extends BaseDaoHbmImpl<MovingTrader>
		implements MovingTraderDao {

	@Override
	public void remove(MovingTrader model) {
		super.remove(model);
		for (Deal d : model.getDeals()) {
			d.setTrader(null);
		}
	}

	@Override
	public List<MovingTrader> getAllByLocation(Location location) {
		log.debug("calling getAllByLocation(" + location + ")");
		List<?> list = sessionFactory.getCurrentSession().getNamedQuery("movingTrader.getAllForLocation")
				.setParameter("location", location)
				.list();

		List<MovingTrader> result = new Vector<>(list.size());
		for (Object o : list) {
			result.add((MovingTrader) o);
		}

		log.trace("returning " + result);
		return result;
	}

	@Override
	public List<MovingTrader> getAllByCategory(TraderCategory category) {
		log.debug("calling getAllByCategory(" + category + ")");
		List<?> list = sessionFactory.getCurrentSession().getNamedQuery("movingTrader.getAllForCategory")
				.setParameter("category", category)
				.list();

		List<MovingTrader> result = new ArrayList<>(list.size());
		for (Object o : list) {
			result.add((MovingTrader) o);
		}

		log.trace("returning " + result);
		return result;
	}
}
