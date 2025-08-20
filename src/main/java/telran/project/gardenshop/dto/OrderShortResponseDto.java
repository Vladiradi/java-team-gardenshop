package telran.project.gardenshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderShortResponseDto {

    private Long id;

    private String status;

    private LocalDateTime createdAt;

    private String deliveryAddress;

    private BigDecimal totalAmount;

    private String contactName;

    private String deliveryMethod;
}
