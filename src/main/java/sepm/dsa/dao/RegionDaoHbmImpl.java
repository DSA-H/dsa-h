package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sepm.dsa.model.Region;

import java.util.List;
import java.util.Vector;

@Repository
public class RegionDaoHbmImpl implements RegionDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public int add(Region region) {
		sessionFactory.getCurrentSession().save(region);
		return region.getId();
	}

	@Override
	public void update(Region region) {
		sessionFactory.getCurrentSession().update(region);
	}

	@Override
	public void remove(Region region) {
		sessionFactory.getCurrentSession().delete(region);
	}

	@Override
	public Region get(int id) {
		Object result = sessionFactory.getCurrentSession().get(Region.class, id);

		if (result == null) {
			return null;
		}

		return (Region) result;
	}

	@Override
	public List<Region> getAll() {
		List<?> list = sessionFactory.getCurrentSession().getNamedQuery("Region.findAll").list();

		List<Region> regions = new Vector<>(list.size());
		for (Object o: list) {
			regions.add((Region) o);
		}

		return regions;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
