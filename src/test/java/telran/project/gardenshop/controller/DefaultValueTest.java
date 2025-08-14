package telran.project.gardenshop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class DefaultValueTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void whenGroupByNotProvided_shouldUseDayAsDefault() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        GroupedProfitReportDto expectedReport = GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy("DAY") // ← Ожидаем DAY как значение по умолчанию
                .groupedData(Arrays.asList())
                .totalRevenue(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .totalOrders(0L)
                .totalItemsSold(0L)
                .build();

        // Mock сервиса ожидает GroupByPeriod.DAY
        when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.DAY)))
                .thenReturn(expectedReport);

        // When & Then
        // Запрос БЕЗ параметра groupBy
        mockMvc.perform(get("/v1/reports/profit/grouped")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupBy").value("DAY")); // ← Проверяем, что используется DAY
    }

    @Test
    void whenGroupByProvided_shouldUseProvidedValue() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        GroupedProfitReportDto expectedReport = GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy("HOUR") // ← Ожидаем HOUR как переданное значение
                .groupedData(Arrays.asList())
                .totalRevenue(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .totalOrders(0L)
                .totalItemsSold(0L)
                .build();

        // Mock сервиса ожидает GroupByPeriod.HOUR
        when(reportService.getGroupedProfitReport(eq(startDate), eq(endDate), eq(GroupByPeriod.HOUR)))
                .thenReturn(expectedReport);

        // When & Then
        // Запрос С параметром groupBy=HOUR
        mockMvc.perform(get("/v1/reports/profit/grouped")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("groupBy", "HOUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupBy").value("HOUR")); // ← Проверяем, что используется HOUR
    }
}
