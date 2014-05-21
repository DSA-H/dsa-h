package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.AssortmentNatureDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("AssortmentNatureService")
@Transactional(readOnly = true)
public class AssortmentNatureServiceImpl implements AssortmentNatureService {

    private static final Logger log = LoggerFactory.getLogger(AssortmentNatureServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();

    private AssortmentNatureDao assortmentNatureDao;

    @Override
    @Transactional(readOnly = false)
    public void add(AssortmentNature assortmentNature) {
        log.debug("calling add(" + assortmentNature + ")");
        validate(assortmentNature);
        assortmentNatureDao.add(assortmentNature);
    }

    @Override
    @Transactional(readOnly = false)
    public void add(HashSet<AssortmentNature> assortmentNature) {
        for (AssortmentNature an : assortmentNature) {
            add(an);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void update(AssortmentNature assortmentNature) {
        log.debug("calling update(" + assortmentNature + ")");
        validate(assortmentNature);
        assortmentNatureDao.update(assortmentNature);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(AssortmentNature assortmentNature) {
        log.debug("calling remove(" + assortmentNature + ")");
        assortmentNatureDao.remove(assortmentNature);
    }

    @Override
    public AssortmentNature get(int id) {
        log.debug("calling get(" + id + ")");
        AssortmentNature result = assortmentNatureDao.get(id);
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

    /**
     * Validates a Location
     *
     * @param assortmentNature must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if assortmentNature is not valid
     */
    private void validate(AssortmentNature assortmentNature) throws DSAValidationException {
        Set<ConstraintViolation<AssortmentNature>> violations = validator.validate(assortmentNature);
        if (violations.size() > 0) {
            throw new DSAValidationException("Assortment Nature ist nicht valide.", violations);
        }
    }

    public void setAssortmentNatureDao(AssortmentNatureDao assortmentNatureDao) {
        this.assortmentNatureDao = assortmentNatureDao;
    }

    public AssortmentNatureDao getAssortmentNatureDao() {
        return assortmentNatureDao;
    }
}
