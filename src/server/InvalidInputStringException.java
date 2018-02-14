package server;

public class InvalidInputStringException extends RuntimeException {
    private String messaage;

    public InvalidInputStringException(String message) {
        super(message);
    }

    public InvalidInputStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputStringException(Throwable cause) {
        super(cause);
    }

    public InvalidInputStringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
