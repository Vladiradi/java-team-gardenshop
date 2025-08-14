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
public class GroupedProfitReportDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String groupBy;

    private List<GroupedProfitData> groupedData;

    private BigDecimal totalRevenue;

    private BigDecimal totalCost;

    private BigDecimal totalProfit;

    private BigDecimal profitMargin;

    private Long totalOrders;

    private Long totalItemsSold;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupedProfitData {

        private String periodLabel;

        private LocalDateTime periodStart;

        private LocalDateTime periodEnd;

        private BigDecimal revenue;

        private BigDecimal cost;

        private BigDecimal profit;

        private BigDecimal profitMargin;

        private Long orderCount;

        private Long itemsSold;
    }
}
