package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Chris on 17.05.2014.
 */
@Repository
@Transactional(readOnly = true)
public class ProductDaoHbmImpl implements ProductDao {

    private static final Logger log = LoggerFactory.getLogger(RegionDaoHbmImpl.class);

    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = false)
    public void add(Product product) {
        log.debug("calling add(" + product + ")");
        sessionFactory.getCurrentSession().save(product);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Product product) {
        log.debug("calling update(" + product + ")");
        sessionFactory.getCurrentSession().update(product);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Product product) {
        log.debug("calling remove(" + product + ")");
        product = get(product.getId());

        product.getOffer().clear();

        sessionFactory.getCurrentSession().delete(product);
    }

    @Override
    public Product get(int id) {
        log.debug("calling get(" + id + ")");

        Object result = sessionFactory.getCurrentSession().get(Product.class, id);

        if (result == null) {
            throw new DSARuntimeException("Leider existiert für diese ID kein Produkt");
        }
        log.trace("returning " + result);
        return (Product) result;
    }

    @Override
    public List<Product> getAll() {
        log.debug("calling getAll()");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("Product.findAll").list();

        List<Product> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Product) o);
        }

        log.trace("returning " + result);
        return result;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        log.debug("calling setSessionFactory(" + sessionFactory + ")");
        this.sessionFactory = sessionFactory;
    }
}
