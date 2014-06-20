package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.AssortmentNatureDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service("AssortmentNatureService")
@Transactional(readOnly = true)
public class AssortmentNatureServiceImpl implements AssortmentNatureService {

    private static final Logger log = LoggerFactory.getLogger(AssortmentNatureServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private AssortmentNatureDao assortmentNatureDao;

    /**
     * Validates a Location
     *
     * @param assortmentNature must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if assortmentNature is not valid
     */
    public void validate(AssortmentNature assortmentNature) throws DSAValidationException {
        Set<ConstraintViolation<AssortmentNature>> violations = validator.validate(assortmentNature);
        if (violations.size() > 0) {
            throw new DSAValidationException("Assortment Nature ist nicht valide.", violations);
        }
    }

    @Override
    public AssortmentNature add(AssortmentNature assortmentNature) {
        log.debug("calling add(" + assortmentNature + ")");
        validate(assortmentNature);
        return assortmentNatureDao.add(assortmentNature);
    }

    @Override
    public AssortmentNature update(AssortmentNature assortmentNature) {
        log.debug("calling update(" + assortmentNature + ")");
        validate(assortmentNature);
        return assortmentNatureDao.update(assortmentNature);
    }

    @Override
    public void remove(AssortmentNature assortmentNature) {
        log.debug("calling remove(" + assortmentNature + ")");
        assortmentNatureDao.remove(assortmentNature);
    }

    @Override
    public AssortmentNature get(TraderCategory traderCategory, ProductCategory productCategory) {
        log.debug("calling get(" + traderCategory + "," + productCategory + ")");
        AssortmentNature.Pk pk = new AssortmentNature.Pk(traderCategory, productCategory);
        AssortmentNature result = assortmentNatureDao.get(pk);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<AssortmentNature> getAll() {
        log.debug("calling getAll()");
        List<AssortmentNature> result = assortmentNatureDao.getAll();
        log.trace("returning " + result);
        return result;

    }

    @Override
    public List<AssortmentNature> getAllByProductCategory(int productCategoryId) {
        log.debug("calling getAllByProductCategory(" + productCategoryId + ")");
        List<AssortmentNature> result = assortmentNatureDao.getAllByProductCategory(productCategoryId);
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<AssortmentNature> getAllByTraderCategory(int traderCategoryId) {
        log.debug("calling getAllByTraderCategory(" + traderCategoryId + ")");
        List<AssortmentNature> result = assortmentNatureDao.getAllByTraderCategory(traderCategoryId);
        log.trace("returning " + result);
        return result;
    }

    public void setAssortmentNatureDao(AssortmentNatureDao assortmentNatureDao) {
        this.assortmentNatureDao = assortmentNatureDao;
    }
}
