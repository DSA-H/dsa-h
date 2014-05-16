package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.RegionBorderPk;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Created by Michael on 11.05.2014.
 */
@Repository
@Transactional(readOnly = true)
public class RegionBorderDaoHbmImpl implements RegionBorderDao, Serializable {

    private static final long serialVersionUID = 1L;

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public RegionBorderPk add(RegionBorder regionBorder) {
        sessionFactory.getCurrentSession().save(regionBorder);
        return regionBorder.getPk();
    }

    @Override
    @Transactional(readOnly = false)
    public void update(RegionBorder regionBorder) {
        sessionFactory.getCurrentSession().update(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
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
    public List<RegionBorder> getAllByRegion(int regionId) {
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
