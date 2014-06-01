package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Deal;
import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class TraderDaoHbmImpl
	extends BaseDaoHbmImpl<Trader>
	implements TraderDao {

    @Override
    public void remove(Trader model) {
        super.remove(model);
        for (Deal d : model.getDeals()) {
            d.setTrader(null);
        }
    }

    @Override
    public List<Trader> getAllByLocation(Location location) {
        log.debug("calling getAllByLocation(" + location + ")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("trader.getAllForLocation")
                .setParameter("location", location)
                .list();

        List<Trader> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Trader) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Trader> getAllByCategory(TraderCategory category) {
        log.debug("calling getAllByCategory(" + category + ")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("trader.getAllForCategory")
                .setParameter("category", category)
                .list();

        List<Trader> result = new ArrayList<>(list.size());
        for (Object o : list) {
            result.add((Trader) o);
        }

        log.trace("returning " + result);
        return result;
    }
}
