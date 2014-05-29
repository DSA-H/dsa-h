package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.ProductUnitDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.ProductUnit;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service("ProductUnitService")
@Transactional(readOnly = true)
public class ProductUnitServiceImpl implements ProductUnitService {
    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private ProductUnitDao productUnitDao;

    @Override
    public ProductUnit get(Integer id) {
        log.debug("calling get(" + id + ")");
        ProductUnit result = productUnitDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public ProductUnit add(ProductUnit p) {
        log.debug("calling addConnection(" + p + ")");
        validate(p);
        return productUnitDao.add(p);
    }

    @Override
    @Transactional(readOnly = false)
    public ProductUnit update(ProductUnit p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        return productUnitDao.update(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(ProductUnit p) {
        log.debug("calling removeConnection(" + p + ")");
        productUnitDao.remove(get(p.getId()));
    }

    @Override
    public List<ProductUnit> getAll() {
        log.debug("calling getAll()");
        List<ProductUnit> result = productUnitDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setProductDao(ProductUnitDao productUnitDao) {
        log.debug("calling setProductDao(" + productUnitDao + ")");
        this.productUnitDao = productUnitDao;
    }

    /**
     * Validates a product
     *
     * @param product
     * @throws sepm.dsa.exceptions.DSAValidationException if product is not valid
     */
    private void validate(ProductUnit product) throws DSAValidationException {
        Set<ConstraintViolation<ProductUnit>> violations = validator.validate(product);
        if (violations.size() > 0) {
            throw new DSAValidationException("Produktunit ist nicht valide.", violations);
        }
    }

    public ProductUnitDao getProductUnitDao() {
        return productUnitDao;
    }

    public void setProductUnitDao(ProductUnitDao productUnitDao) {
        this.productUnitDao = productUnitDao;
    }
}
