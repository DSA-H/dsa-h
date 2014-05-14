package sepm.dsa.exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michael on 14.05.2014.
 */
public class DSAValidationException extends DSARuntimeException {

    private List<?> constraintViolations;

    public DSAValidationException(String msg, Collection<?> constraintViolations) {
        super(msg, null, ERROR_VALIDATION);

        this.constraintViolations = new ArrayList<>(constraintViolations);
    }

    public List<?> getConstraintViolations() {
        return new ArrayList<>(constraintViolations);
    }
}
