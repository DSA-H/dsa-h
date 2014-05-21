package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
}
