package sepm.dsa.exceptions;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michael on 14.05.2014.
 */
public class DSAValidationException extends DSARuntimeException {

    private List<ConstraintViolation<?>> constraintViolations;

    public DSAValidationException(String msg, Collection<? extends ConstraintViolation<?>> constraintViolations) {
        super(msg, null, ERROR_VALIDATION);

        this.constraintViolations = new ArrayList<>(constraintViolations);
    }

    public List<ConstraintViolation> getConstraintViolations() {
        return new ArrayList<>(constraintViolations);
    }
}
