package sepm.dsa.dao;

import org.hibernate.Query;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.model.Region;

import java.util.List;
import java.util.Vector;

public class RegionDaoHbmImpl
	extends BaseDaoHbmImpl<Region>
	implements RegionDao {

	@Override
	public void remove(Region region) {
		super.remove(region);

		region.getAllBorders().forEach(rb -> {
			Region r = rb.getRegion1().equals(region) ? rb.getRegion2() : rb.getRegion1();
			r.getBorders1().remove(rb);
			r.getBorders2().remove(rb);
		});

	}

    @Override
    public List<Region> getAllByPreferredCurrencySet(CurrencySet currencySet) {
        log.debug("calling getAllByPreferredCurrencySet(" + currencySet + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Region.findAllByPreferredCurrencySet");
        query.setParameter("preferredCurrencySetId", currencySet == null ? null : currencySet.getId());
        List<?> list = query.list();

        List<Region> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Region) o);
        }
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Region> getAll() {
        log.debug("calling getAll()");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Region.findAll");
        List<?> list = query.list();

        List<Region> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Region) o);
        }
        log.trace("returning " + result);
        return result;
    }
}
