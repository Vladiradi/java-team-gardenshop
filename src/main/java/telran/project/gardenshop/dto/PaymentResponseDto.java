package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private Long id;
    private Long orderId;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDateTime createdAt;
}
