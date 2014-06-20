package sepm.dsa.util;

import javax.validation.ConstraintViolation;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Michael on 14.05.2014.
 */
public class ValidationMessageUtil {

    private final static String BEAN_PROPERTIES_FILE = "BeanProperties";

    public static String errorMsg(ConstraintViolation<?> v) {
        return getPropertyName(propertyIdentifier(v)) + " " + v.getMessage();
    }

    public static String propertyIdentifier(ConstraintViolation<?> v) {
        return v.getRootBeanClass().getSimpleName() + "." + v.getPropertyPath();
    }

    public static String getPropertyName(String key) {
        return getPropertyName(key, null);
    }

    private static String getPropertyName(String key, Locale locale) {
        try {
            if (locale == null) {
                return ResourceBundle.getBundle(BEAN_PROPERTIES_FILE).getString(key);
            }
            return ResourceBundle.getBundle(BEAN_PROPERTIES_FILE, locale).getString(key);
        } catch (java.util.MissingResourceException ex) {
            return key;
        }
    }
}
