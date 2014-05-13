package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.RegionBorderPk;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Created by Michael on 11.05.2014.
 */
@Repository
public class RegionBorderDaoHbmImpl implements RegionBorderDao, Serializable {

    private SessionFactory sessionFactory;

    @Override
    public RegionBorderPk add(RegionBorder regionBorder) {
        sessionFactory.getCurrentSession().save(regionBorder);
        return regionBorder.getPk();
    }

    @Override
    public void update(RegionBorder regionBorder) {
        sessionFactory.getCurrentSession().update(regionBorder);
    }

    @Override
    public void remove(RegionBorder regionBorder) {
        sessionFactory.getCurrentSession().delete(regionBorder);
    }

    @Override
    public RegionBorder get(RegionBorderPk pk) {
        Object result = sessionFactory.getCurrentSession().get(RegionBorder.class, pk);
        if (result == null) {
            return null;
        }
        return (RegionBorder) result;
    }

    @Override
    public List<RegionBorder> getAll() {
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("RegionBorder.findAll").list();

        List<RegionBorder> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((RegionBorder) o);
        }

        return result;
    }

    @Override
    public List<RegionBorder> getAllForRegion(int regionId) {
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("RegionBorder.findAllForRegion")
                .setParameter("regionId", regionId)
                .list();

        List<RegionBorder> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((RegionBorder) o);
        }

        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
