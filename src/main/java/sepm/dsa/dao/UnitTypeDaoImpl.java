package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.UnitType;

import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class UnitTypeDaoImpl  implements UnitTypeDao{

    private static final Logger log = LoggerFactory.getLogger(UnitTypeDaoImpl.class);
    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public int add(UnitType unitType) {

        log.debug("calling add(" + unitType + ")");
        sessionFactory.getCurrentSession().save(unitType);
        return unitType.getId();
    }

    @Override
    @Transactional(readOnly = false)
    public void update(UnitType unitType) {
        log.debug("calling update(" + unitType + ")");
        sessionFactory.getCurrentSession().update(unitType);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(UnitType unitType) {
        log.debug("calling remove(" + unitType + ")");
        sessionFactory.getCurrentSession().delete(unitType);
    }

    @Override
    public UnitType get(Integer id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(UnitType.class, id);

        if (result == null) {
            return null;
        }
        log.trace("returning " + result);
        return (UnitType) result;
    }

    @Override
    public List<UnitType> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("UnitType.findAll").list();

        List<UnitType> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((UnitType) o);
        }

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
