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
public class ProfitReportDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal totalRevenue;

    private BigDecimal totalCost;

    private BigDecimal totalProfit;

    private BigDecimal profitMargin;

    private Long totalOrders;

    private Long totalItemsSold;
} 