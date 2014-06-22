package sepm.dsa.dao;

import org.hibernate.Query;
import sepm.dsa.model.Tavern;

import java.util.List;
import java.util.Vector;

public class TavernDaoHbmImpl
	extends BaseDaoHbmImpl<Tavern>
	implements TavernDao {

    @Override
    public List<Tavern> getAllByLocation(int locationId) {
        log.debug("calling getAll()");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Tavern.findAllByLocation");
        query.setParameter("locationId", locationId);
        List<?> list = query.list();

        List<Tavern> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Tavern) o);
        }

        log.trace("returning " + result);
        return result;
    }

}
