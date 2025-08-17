package telran.project.gardenshop.exception;

public class InvalidDiscountDataException extends RuntimeException {

    public InvalidDiscountDataException(String message) {
        super(message);
    }

    public InvalidDiscountDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
