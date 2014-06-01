package sepm.dsa.util;

import javax.validation.ConstraintViolation;

/**
 * Created by Michael on 14.05.2014.
 */
public class ValidationMessageUtil {

    public static String errorMsg(ConstraintViolation<?> v) {
        return propertyIdentifier(v) + " " + v.getMessage();
    }

    public static String propertyIdentifier(ConstraintViolation<?> v) {
        return v.getRootBeanClass().getSimpleName() + "." + v.getPropertyPath();
    }

}
