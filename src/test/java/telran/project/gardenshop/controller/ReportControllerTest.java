package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.CancelledProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.service.ReportService;
import telran.project.gardenshop.service.security.JwtFilter;
import telran.project.gardenshop.service.security.JwtService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void getTopProductsBySales_ShouldReturnTopProducts() throws Exception {
        // Given
        List<ProductReportDto> topProducts = Arrays.asList(
                ProductReportDto.builder()
                        .productId(1L)
                        .productName("Product 1")
                        .totalQuantitySold(10L)
                        .totalRevenue(BigDecimal.valueOf(1000))
                        .build(),
                ProductReportDto.builder()
                        .productId(2L)
                        .productName("Product 2")
                        .totalQuantitySold(5L)
                        .totalRevenue(BigDecimal.valueOf(500))
                        .build()
        );

        when(reportService.getTopProductsBySales(5)).thenReturn(topProducts);

        // When & Then
        mockMvc.perform(get("/v1/reports/top-products")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                .andExpect(jsonPath("$[0].totalQuantitySold").value(10))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                .andExpect(jsonPath("$[1].totalQuantitySold").value(5));
    }

    @Test
    void getTopCancelledProducts_ShouldReturnTopCancelledProducts() throws Exception {
        // Given
        List<CancelledProductReportDto> topCancelledProducts = Arrays.asList(
                CancelledProductReportDto.builder()
                        .productId(1L)
                        .productName("Product 1")
                        .totalQuantityCancelled(5L)
                        .cancellationCount(3L)
                        .build(),
                CancelledProductReportDto.builder()
                        .productId(2L)
                        .productName("Product 2")
                        .totalQuantityCancelled(3L)
                        .cancellationCount(2L)
                        .build()
        );

        when(reportService.getTopProductsByCancellations(5)).thenReturn(topCancelledProducts);

        // When & Then
        mockMvc.perform(get("/v1/reports/top-cancelled-products")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                .andExpect(jsonPath("$[0].totalQuantityCancelled").value(5))
                .andExpect(jsonPath("$[0].cancellationCount").value(3))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                .andExpect(jsonPath("$[1].totalQuantityCancelled").value(3))
                .andExpect(jsonPath("$[1].cancellationCount").value(2));
    }

    @Test
    void getProfitReport() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        ProfitReportDto profitReport = ProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(BigDecimal.valueOf(10000))
                .totalCost(BigDecimal.valueOf(6000))
                .totalProfit(BigDecimal.valueOf(4000))
                .profitMargin(BigDecimal.valueOf(40.0))
                .totalOrders(100L)
                .totalItemsSold(500L)
                .build();

        when(reportService.getProfitReport(eq(startDate), eq(endDate))).thenReturn(profitReport);

        // When & Then
        mockMvc.perform(get("/v1/reports/profit")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(10000))
                .andExpect(jsonPath("$.totalCost").value(6000))
                .andExpect(jsonPath("$.totalProfit").value(4000))
                .andExpect(jsonPath("$.profitMargin").value(40.0))
                .andExpect(jsonPath("$.totalOrders").value(100))
                .andExpect(jsonPath("$.totalItemsSold").value(500));
    }

    @Test
    void getGroupedProfitReport_WithDayGrouping_ShouldReturnGroupedData() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        String groupBy = "DAY";

        GroupedProfitReportDto.GroupedProfitData dayData1 = GroupedProfitReportDto.GroupedProfitData.builder()
                .periodLabel("2024-01-15")
                .periodStart(LocalDateTime.of(2024, 1, 15, 0, 0))
                .periodEnd(LocalDateTime.of(2024, 1, 15, 23, 59, 59))
                .revenue(BigDecimal.valueOf(1000))
                .cost(BigDecimal.valueOf(600))
                .profit(BigDecimal.valueOf(400))
                .profitMargin(BigDecimal.valueOf(40.0))
                .orderCount(5L)
                .itemsSold(20L)
                .build();

        GroupedProfitReportDto.GroupedProfitData dayData2 = GroupedProfitReportDto.GroupedProfitData.builder()
                .periodLabel("2024-01-16")
                .periodStart(LocalDateTime.of(2024, 1, 16, 0, 0))
                .periodEnd(LocalDateTime.of(2024, 1, 16, 23, 59, 59))
                .revenue(BigDecimal.valueOf(1500))
                .cost(BigDecimal.valueOf(900))
                .profit(BigDecimal.valueOf(600))
                .profitMargin(BigDecimal.valueOf(40.0))
                .orderCount(7L)
                .itemsSold(30L)
                .build();

        GroupedProfitReportDto groupedReport = GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy("DAY")
                .groupedData(Arrays.asList(dayData1, dayData2))
                .totalRevenue(BigDecimal.valueOf(2500))
                .totalCost(BigDecimal.valueOf(1500))
                .totalProfit(BigDecimal.valueOf(1000))
                .profitMargin(BigDecimal.valueOf(40.0))
                .totalOrders(12L)
                .totalItemsSold(50L)
                .build();

        when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.DAY))).thenReturn(groupedReport);

        // When & Then
        mockMvc.perform(get("/v1/reports/profit/grouped")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("groupBy", groupBy))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupBy").value("DAY"))
                .andExpect(jsonPath("$.totalRevenue").value(2500))
                .andExpect(jsonPath("$.totalProfit").value(1000))
                .andExpect(jsonPath("$.groupedData").isArray())
                .andExpect(jsonPath("$.groupedData.length()").value(2))
                .andExpect(jsonPath("$.groupedData[0].periodLabel").value("2024-01-15"))
                .andExpect(jsonPath("$.groupedData[0].revenue").value(1000))
                .andExpect(jsonPath("$.groupedData[1].periodLabel").value("2024-01-16"))
                .andExpect(jsonPath("$.groupedData[1].revenue").value(1500));
    }

    @Test
    void getGroupedProfitReport_WithHourGrouping_ShouldReturnGroupedData() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        String groupBy = "HOUR";

        GroupedProfitReportDto.GroupedProfitData hourData = GroupedProfitReportDto.GroupedProfitData.builder()
                .periodLabel("2024-01-15 14:00")
                .periodStart(LocalDateTime.of(2024, 1, 15, 14, 0))
                .periodEnd(LocalDateTime.of(2024, 1, 15, 15, 0))
                .revenue(BigDecimal.valueOf(500))
                .cost(BigDecimal.valueOf(300))
                .profit(BigDecimal.valueOf(200))
                .profitMargin(BigDecimal.valueOf(40.0))
                .orderCount(3L)
                .itemsSold(12L)
                .build();

        GroupedProfitReportDto groupedReport = GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy("HOUR")
                .groupedData(Arrays.asList(hourData))
                .totalRevenue(BigDecimal.valueOf(500))
                .totalCost(BigDecimal.valueOf(300))
                .totalProfit(BigDecimal.valueOf(200))
                .profitMargin(BigDecimal.valueOf(40.0))
                .totalOrders(3L)
                .totalItemsSold(12L)
                .build();

        when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.HOUR))).thenReturn(groupedReport);

        // When & Then
        mockMvc.perform(get("/v1/reports/profit/grouped")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("groupBy", groupBy))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupBy").value("HOUR"))
                .andExpect(jsonPath("$.groupedData[0].periodLabel").value("2024-01-15 14:00"));
    }

    @Test
    void getGroupedProfitReport_WithDefaultGrouping_ShouldUseDayGrouping() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        GroupedProfitReportDto groupedReport = GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy("DAY")
                .groupedData(Arrays.asList())
                .totalRevenue(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .totalOrders(0L)
                .totalItemsSold(0L)
                .build();

        when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.DAY))).thenReturn(groupedReport);

        // When & Then
        mockMvc.perform(get("/v1/reports/profit/grouped")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupBy").value("DAY"));
    }

    @Test
    void getGroupedProfitReport_WithInvalidGroupBy_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        String invalidGroupBy = "INVALID";

        // When & Then
        mockMvc.perform(get("/v1/reports/profit/grouped")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("groupBy", invalidGroupBy))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPendingPaymentOrders() throws Exception {
        // Given
        List<PendingPaymentReportDto> pendingOrders = Arrays.asList(
                PendingPaymentReportDto.builder()
                        .orderId(1L)
                        .userId(1L)
                        .userEmail("test@example.com")
                        .userFullName("Test User")
                        .orderCreatedAt(LocalDateTime.now().minusDays(10))
                        .daysPending(10L)
                        .orderTotal(BigDecimal.valueOf(500))
                        .items(Arrays.asList())
                        .build()
        );

        when(reportService.getPendingPaymentOrders(7)).thenReturn(pendingOrders);

        // When & Then
        mockMvc.perform(get("/v1/reports/pending-payments")
                        .param("daysOlder", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].userEmail").value("test@example.com"))
                .andExpect(jsonPath("$[0].userFullName").value("Test User"))
                .andExpect(jsonPath("$[0].daysPending").value(10))
                .andExpect(jsonPath("$[0].orderTotal").value(500));
    }
}
