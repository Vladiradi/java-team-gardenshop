package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfDayDto {

    private Long productId;
    private String productName;
    private String description;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal savingsAmount;
    private String savingsPercentage;
    private LocalDateTime discountEndDate;
    private Long timeRemainingInSeconds; // Время до окончания скидки в секундах
    private String categoryName;
}
