package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private DiscountType type;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isCurrentlyActive;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal savingsAmount;
    private String savingsPercentage;
}
