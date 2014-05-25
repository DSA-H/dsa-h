package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.ProductCategory;

import java.util.List;
import java.util.Vector;

/**
 * Created by Chris on 17.05.2014.
 */
@Repository
@Transactional(readOnly = true)
public class ProductCategoryDaoHbmImpl implements ProductCategoryDao {

    private static final Logger log = LoggerFactory.getLogger(RegionDaoHbmImpl.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public int add(ProductCategory category) {
        log.debug("calling add(" + category + ")");
        sessionFactory.getCurrentSession().save(category);
        return category.getId();
    }

    @Override
    @Transactional(readOnly = false)
    public void update(ProductCategory category) {
        log.debug("calling update(" + category + ")");
        sessionFactory.getCurrentSession().update(category);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(ProductCategory category) {
        log.debug("calling remove(" + category + ")");
        sessionFactory.getCurrentSession().delete(category);
    }

    @Override
    public ProductCategory get(Integer id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(ProductCategory.class, id);

        if (result == null) {
            return null;
        }
        log.trace("returning " + result);
        return (ProductCategory) result;
    }

    @Override
    public List<ProductCategory> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("ProductCategory.findAll").list();

        List<ProductCategory> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((ProductCategory) o);
        }

	    log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
