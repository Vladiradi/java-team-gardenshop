package telran.project.gardenshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDiscountDto {

    // Прямая скидочная цена (приоритет выше)
    private Double discountPrice;

    // Процент скидки (0-100)
    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "99.99", message = "Discount percentage must be less than 100")
    private Double discountPercentage;
}
