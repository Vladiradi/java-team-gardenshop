package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelledProductReportDto {

    private Long productId;

    private String productName;

    private String productImageUrl;

    private BigDecimal productPrice;

    private Long totalQuantityCancelled;
}
