package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Product;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * Created by Chris on 17.05.2014.
 */
@Service("ProductService")
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService, Serializable {
    private static final long serialVersionUID = 7415861483489569621L;
    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private ProductDao productDao;

    @Override
    public Product get(Integer id) {
        log.debug("calling get(" + id + ")");
        Product result = productDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public int add(Product p) {
        log.debug("calling add(" + p + ")");
        validate(p);
        return productDao.add(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Product p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        productDao.update(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Product p) {
        log.debug("calling remove(" + p + ")");
        productDao.remove(get(p.getId()));
    }

    @Override
    public List<Product> getAll() {
        log.debug("calling getAll()");
        List<Product> result = productDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setProductDao(ProductDao productDao) {
        log.debug("calling setProductDao(" + productDao + ")");
        this.productDao = productDao;
    }

    /**
     * Validates a product
     * @param product
     * @throws sepm.dsa.exceptions.DSAValidationException if product is not valid
     */
    private void validate(Product product) throws DSAValidationException {
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (violations.size() > 0) {
            throw new DSAValidationException("Produkt ist nicht valide.", violations);
        }
    }

}
