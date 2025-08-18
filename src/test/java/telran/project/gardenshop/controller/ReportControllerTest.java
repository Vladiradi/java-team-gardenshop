package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.enums.ProductReportType;
import telran.project.gardenshop.service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest extends AbstractTest {

        private MockMvc mockMvc;

        @Mock
        private ReportService reportService;

        @InjectMocks
        private ReportController reportController;

        private ObjectMapper objectMapper;

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("GET /v1/reports/top-products - Get top products by sales")
        void getTopProductsByType_Sales_ShouldReturnTopProducts() throws Exception {
                List<ProductReportDto> topProducts = Arrays.asList(
                                ProductReportDto.builder()
                                                .productId(1L)
                                                .productName("Product 1")
                                                .totalQuantity(10L)
                                                .totalRevenue(BigDecimal.valueOf(1000))
                                                .reportType(ProductReportType.SALES)
                                                .build(),
                                ProductReportDto.builder()
                                                .productId(2L)
                                                .productName("Product 2")
                                                .totalQuantity(5L)
                                                .totalRevenue(BigDecimal.valueOf(500))
                                                .reportType(ProductReportType.SALES)
                                                .build());

                when(reportService.getTopProductsByType(ProductReportType.SALES, 5)).thenReturn(topProducts);

                mockMvc.perform(get("/v1/reports/top-products")
                                .param("reportType", "SALES")
                                .param("limit", "5"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].productId").value(1))
                                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                                .andExpect(jsonPath("$[0].totalQuantity").value(10))
                                .andExpect(jsonPath("$[0].reportType").value("SALES"))
                                .andExpect(jsonPath("$[1].productId").value(2))
                                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                                .andExpect(jsonPath("$[1].totalQuantity").value(5))
                                .andExpect(jsonPath("$[1].reportType").value("SALES"));
        }

        @Test
        @DisplayName("GET /v1/reports/top-products - Get top cancelled products")
        void getTopProductsByType_Cancellations_ShouldReturnTopCancelledProductsByQuantity() throws Exception {
                List<ProductReportDto> topCancelledProducts = Arrays.asList(
                                ProductReportDto.builder()
                                                .productId(1L)
                                                .productName("Product 1")
                                                .totalQuantity(10L)
                                                .totalRevenue(BigDecimal.ZERO)
                                                .reportType(ProductReportType.CANCELLATIONS)
                                                .build(),
                                ProductReportDto.builder()
                                                .productId(2L)
                                                .productName("Product 2")
                                                .totalQuantity(5L)
                                                .totalRevenue(BigDecimal.ZERO)
                                                .reportType(ProductReportType.CANCELLATIONS)
                                                .build());

                when(reportService.getTopProductsByType(ProductReportType.CANCELLATIONS, 5))
                                .thenReturn(topCancelledProducts);

                mockMvc.perform(get("/v1/reports/top-products")
                                .param("reportType", "CANCELLATIONS")
                                .param("limit", "5"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].productId").value(1))
                                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                                .andExpect(jsonPath("$[0].totalQuantity").value(10))
                                .andExpect(jsonPath("$[0].reportType").value("CANCELLATIONS"))
                                .andExpect(jsonPath("$[1].productId").value(2))
                                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                                .andExpect(jsonPath("$[1].totalQuantity").value(5))
                                .andExpect(jsonPath("$[1].reportType").value("CANCELLATIONS"));
        }

        @Test
        @DisplayName("GET /v1/reports/top-products - Get default sales report")
        void getTopProductsByType_Default_ShouldReturnSalesReport() throws Exception {
                List<ProductReportDto> topProducts = Arrays.asList(
                                ProductReportDto.builder()
                                                .productId(1L)
                                                .productName("Product 1")
                                                .totalQuantity(10L)
                                                .totalRevenue(BigDecimal.valueOf(1000))
                                                .reportType(ProductReportType.SALES)
                                                .build());

                when(reportService.getTopProductsByType(ProductReportType.SALES, 10)).thenReturn(topProducts);

                mockMvc.perform(get("/v1/reports/top-products")
                                .param("limit", "10"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].productId").value(1))
                                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                                .andExpect(jsonPath("$[0].totalQuantity").value(10))
                                .andExpect(jsonPath("$[0].reportType").value("SALES"));
        }

        @Test
        @DisplayName("GET /v1/reports/profit - Get profit report")
        void getProfitReport() throws Exception {
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

                mockMvc.perform(get("/v1/reports/profit")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString()))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalRevenue").value(10000))
                                .andExpect(jsonPath("$.totalCost").value(6000))
                                .andExpect(jsonPath("$.totalProfit").value(4000))
                                .andExpect(jsonPath("$.profitMargin").value(40.0))
                                .andExpect(jsonPath("$.totalOrders").value(100))
                                .andExpect(jsonPath("$.totalItemsSold").value(500));
        }

        @Test
        @DisplayName("GET /v1/reports/profit/grouped - Get grouped profit report by day")
        void getGroupedProfitReport_WithDayGrouping_ShouldReturnGroupedData() throws Exception {
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

                when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.DAY)))
                                .thenReturn(groupedReport);

                mockMvc.perform(get("/v1/reports/profit/grouped")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("groupBy", groupBy))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.groupBy").value("DAY"))
                                .andExpect(jsonPath("$.groupedData[0].periodLabel").value("2024-01-15"))
                                .andExpect(jsonPath("$.groupedData[1].periodLabel").value("2024-01-16"));
        }

        @Test
        @DisplayName("GET /v1/reports/profit/grouped - Get grouped profit report by hour")
        void getGroupedProfitReport_WithHourGrouping_ShouldReturnGroupedData() throws Exception {
                LocalDateTime startDate = LocalDateTime.now().minusDays(1);
                LocalDateTime endDate = LocalDateTime.now();
                String groupBy = "HOUR";

                GroupedProfitReportDto.GroupedProfitData hourData = GroupedProfitReportDto.GroupedProfitData.builder()
                                .periodLabel("2024-01-15 14:00")
                                .periodStart(LocalDateTime.of(2024, 1, 15, 14, 0))
                                .periodEnd(LocalDateTime.of(2024, 1, 15, 14, 59, 59))
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

                when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.HOUR)))
                                .thenReturn(groupedReport);

                mockMvc.perform(get("/v1/reports/profit/grouped")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("groupBy", groupBy))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.groupBy").value("HOUR"))
                                .andExpect(jsonPath("$.groupedData[0].periodLabel").value("2024-01-15 14:00"));
        }

        @Test
        @DisplayName("GET /v1/reports/profit/grouped - Get grouped profit report with default grouping")
        void getGroupedProfitReport_WithDefaultGrouping_ShouldUseDayGrouping() throws Exception {
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

                when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.DAY)))
                                .thenReturn(groupedReport);

                mockMvc.perform(get("/v1/reports/profit/grouped")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString()))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.groupBy").value("DAY"));
        }

        @Test
        @DisplayName("GET /v1/reports/profit/grouped - Get grouped profit report with invalid grouping")
        void getGroupedProfitReport_WithInvalidGroupBy_ShouldReturnBadRequest() throws Exception {
                LocalDateTime startDate = LocalDateTime.now().minusDays(1);
                LocalDateTime endDate = LocalDateTime.now();
                String invalidGroupBy = "INVALID";

                mockMvc.perform(get("/v1/reports/profit/grouped")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("groupBy", invalidGroupBy))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /v1/reports/pending-payments - Get pending payment orders")
        void getPendingPaymentOrders() throws Exception {
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
                                                .build());

                when(reportService.getPendingPaymentOrders(7)).thenReturn(pendingOrders);

                mockMvc.perform(get("/v1/reports/pending-payments")
                                .param("daysOlder", "7"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].orderId").value(1))
                                .andExpect(jsonPath("$[0].userId").value(1))
                                .andExpect(jsonPath("$[0].userEmail").value("test@example.com"))
                                .andExpect(jsonPath("$[0].userFullName").value("Test User"))
                                .andExpect(jsonPath("$[0].daysPending").value(10))
                                .andExpect(jsonPath("$[0].orderTotal").value(500));
        }
}
