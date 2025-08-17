package telran.project.gardenshop.exception;

public class InvalidDiscountPriceException extends RuntimeException {

    public InvalidDiscountPriceException(String message) {
        super(message);
    }

    public InvalidDiscountPriceException(String message, Throwable cause) {
        super(message, cause);
    }
}
