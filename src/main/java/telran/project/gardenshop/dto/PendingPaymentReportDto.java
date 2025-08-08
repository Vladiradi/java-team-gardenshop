package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingPaymentReportDto {

    private Long orderId;

    private Long userId;

    private String userEmail;

    private String userFullName;

    private LocalDateTime orderCreatedAt;

    private Long daysPending;

    private BigDecimal orderTotal;

    private List<OrderItemResponseDto> items;

} 