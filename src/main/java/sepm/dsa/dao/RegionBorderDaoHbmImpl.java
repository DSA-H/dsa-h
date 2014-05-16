package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RegionBorderDaoHbmImpl.class);
    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public RegionBorderPk add(RegionBorder regionBorder) {
        log.debug("calling add(" + regionBorder + ")");
        sessionFactory.getCurrentSession().save(regionBorder);
        log.trace("returning " + regionBorder.getPk());
        return regionBorder.getPk();
    }

    @Override
    @Transactional(readOnly = false)
    public void update(RegionBorder regionBorder) {
        log.debug("calling update(" + regionBorder + ")");
        sessionFactory.getCurrentSession().update(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(RegionBorder regionBorder) {
        log.debug("calling remove(" + regionBorder + ")");
        sessionFactory.getCurrentSession().delete(regionBorder);
    }

    @Override
    public RegionBorder get(RegionBorderPk pk) {
        log.debug("calling get(" + pk + ")");
        Object result = sessionFactory.getCurrentSession().get(RegionBorder.class, pk);
        if (result == null) {
            return null;
        }
        log.trace("returning " + result);
        return (RegionBorder) result;
    }

    @Override
    public List<RegionBorder> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("RegionBorder.findAll").list();
        List<RegionBorder> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((RegionBorder) o);
        }
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<RegionBorder> getAllForRegion(int regionId) {
        log.debug("calling getAllForRegion(" + regionId + ")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("RegionBorder.findAllForRegion")
                .setParameter("regionId", regionId)
                .list();

        List<RegionBorder> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((RegionBorder) o);
        }
        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory");
        this.sessionFactory = sessionFactory;
    }
}