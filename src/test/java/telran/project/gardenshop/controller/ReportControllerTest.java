package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
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
                        .rank(1)
                        .build(),
                ProductReportDto.builder()
                        .productId(2L)
                        .productName("Product 2")
                        .totalQuantitySold(5L)
                        .totalRevenue(BigDecimal.valueOf(500))
                        .rank(2)
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
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                .andExpect(jsonPath("$[1].totalQuantitySold").value(5))
                .andExpect(jsonPath("$[1].rank").value(2));
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
                .andExpect(jsonPath("$.totalOrders").value(100))
                .andExpect(jsonPath("$.totalItemsSold").value(500));
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