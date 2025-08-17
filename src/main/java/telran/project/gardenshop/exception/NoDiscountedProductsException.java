package telran.project.gardenshop.exception;

public class NoDiscountedProductsException extends RuntimeException {
    
    public NoDiscountedProductsException(String message) {
        super(message);
    }
    
    public NoDiscountedProductsException(String message, Throwable cause) {
        super(message, cause);
    }
}
