package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.Location;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class AssortmentNatureDaoImpl implements AssortmentNatureDao {

    private static final Logger log = LoggerFactory.getLogger(AssortmentNatureDaoImpl.class);
    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(AssortmentNature assortmentNature) {
        log.debug("calling add(" + assortmentNature + ")");
        sessionFactory.getCurrentSession().save(assortmentNature);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(AssortmentNature assortmentNature) {
        log.debug("calling update(" + assortmentNature + ")");
        sessionFactory.getCurrentSession().update(assortmentNature);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(AssortmentNature assortmentNature) {
        log.debug("calling remove(" + assortmentNature + ")");
        sessionFactory.getCurrentSession().delete(assortmentNature);
    }

    @Override
    public AssortmentNature get(int id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(AssortmentNature.class, id);

        if (result == null) {
            log.trace("returning " + result);
            return null;
        }
        log.trace("returning " + result);
        return (AssortmentNature) result;
    }

    @Override
    public List<AssortmentNature> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("AssortmentNature.findAll").list();

        List<AssortmentNature> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((AssortmentNature) o);
        }

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
