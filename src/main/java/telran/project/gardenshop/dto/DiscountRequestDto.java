package telran.project.gardenshop.dto;

import jakarta.validation.constraints.*;
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
public class DiscountRequestDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Discount type is required")
    private DiscountType type;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true; // Validation will be handled by @NotNull
        }
        return endDate.isAfter(startDate);
    }

    @AssertTrue(message = "Percentage discount cannot exceed 100%")
    public boolean isPercentageValid() {
        if (type == DiscountType.PERCENTAGE && discountValue != null) {
            return discountValue.compareTo(BigDecimal.valueOf(100)) <= 0;
        }
        return true;
    }
}
