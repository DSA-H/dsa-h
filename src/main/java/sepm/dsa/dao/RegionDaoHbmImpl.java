package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARegionNotExistingException;
import sepm.dsa.model.Region;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class RegionDaoHbmImpl implements RegionDao {

    private static final Logger log = LoggerFactory.getLogger(RegionDaoHbmImpl.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(Region region) {
        log.debug("calling add(" + region + ")");
        sessionFactory.getCurrentSession().save(region);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Region region) {
        log.debug("calling update(" + region + ")");
        sessionFactory.getCurrentSession().update(region);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Region region) {
        log.debug("calling remove(" + region + ")");
        sessionFactory.getCurrentSession().delete(region);
    }

    @Override
    public Region get(int id) throws DSARegionNotExistingException {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(Region.class, id);

        if (result == null) {
            throw new DSARegionNotExistingException();
        }
        log.trace("returning " + result);
        return (Region) result;
    }

    @Override
    public List<Region> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("Region.findAll").list();

        List<Region> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Region) o);
        }

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
