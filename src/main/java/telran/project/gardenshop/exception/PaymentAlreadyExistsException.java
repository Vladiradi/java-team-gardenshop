package telran.project.gardenshop.exception;

public class PaymentAlreadyExistsException extends RuntimeException {
  public PaymentAlreadyExistsException(String message) {
    super(message);
  }
  public PaymentAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}