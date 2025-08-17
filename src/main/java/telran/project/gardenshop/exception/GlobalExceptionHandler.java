package telran.project.gardenshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import telran.project.gardenshop.dto.ApiResponse;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EmptyCartException.class,
            ProductNotInCartException.class,
            InsufficientQuantityException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse> handleBadRequestExceptions(RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        return new ResponseEntity<>(
                ApiResponse.error(exception, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ProductNotFoundException.class,
            CategoryNotFoundException.class,
            FavoriteNotFoundException.class,
            OrderNotFoundException.class,
            PaymentNotFoundException.class,
            CartNotFoundException.class
    })
    public ResponseEntity<ApiResponse> handleNotFoundException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(
                ApiResponse.error(exception, HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            FavoriteAlreadyExistsException.class,
            PaymentAlreadyExistsException.class,
            UserWithEmailAlreadyExistsException.class
    })
    public ResponseEntity<ApiResponse> handleConflictExceptions(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(
                ApiResponse.error(exception, HttpStatus.CONFLICT.value()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        Map<String, String> messages = exception.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 + ". " + msg2));

        return new ResponseEntity<>(
                ApiResponse.error(exception, messages, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(
                ApiResponse.error(exception, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
