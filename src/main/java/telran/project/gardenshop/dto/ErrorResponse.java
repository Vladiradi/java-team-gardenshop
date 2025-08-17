package telran.project.gardenshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Builder(toBuilder = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String exception;
    private String message;
    private Map<String, String> messages;
    private Integer status;
    private LocalDateTime timestamp;

    public static ErrorResponse error(Exception exception, Integer status) {
        return ErrorResponse.builder()
                .exception(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse error(Exception exception, Map<String, String> messages, Integer status) {
        return ErrorResponse.builder()
                .exception(exception.getClass().getSimpleName())
                .messages(messages)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
