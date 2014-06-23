package sepm.dsa.exceptions;

/**
 * Created by Michael on 14.05.2014.
 */
public class DSARuntimeException extends RuntimeException {

    public final static int ERROR_GENERAL = 0;
    public final static int ERROR_VALIDATION = 10;
    public final static int ERROR_INTERNAL_UNKNOWN_GENERAL = 20;
    public final static int ERROR_INTERNAL_UNKNOWN_DATABASE = 60;

    public final static String INTERNAL_ERROR_MSG = "Ein interner Fehler ist aufgetreten.";

    private int errorCode;

    public DSARuntimeException(String msg, Throwable cause, int errorCode) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public DSARuntimeException(String msg, Throwable cause) {
        this(msg, cause, ERROR_GENERAL);
    }

    public DSARuntimeException(String msg) {
        this(msg, null, ERROR_GENERAL);
    }

    public DSARuntimeException() {
        this(INTERNAL_ERROR_MSG, null, ERROR_GENERAL);
    }

    public DSARuntimeException(Throwable cause) {
        this(INTERNAL_ERROR_MSG, cause, ERROR_GENERAL);
    }

    public int getErrorCode() {
        return errorCode;
    }


}
