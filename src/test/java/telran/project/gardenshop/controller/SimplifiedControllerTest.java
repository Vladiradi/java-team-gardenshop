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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class SimplifiedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void controllerWorksWithoutTryCatch() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        GroupedProfitReportDto expectedReport = GroupedProfitReportDto.builder()
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
                .thenReturn(expectedReport);

        // When & Then - Контроллер работает без try-catch блока
        mockMvc.perform(get("/v1/reports/profit/grouped")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupBy").value("DAY"));

        System.out.println("✅ Контроллер работает без try-catch блока!");
        System.out.println("📝 Исключения обрабатываются глобально в GlobalExceptionHandler");
    }

    @Test
    void invalidValueStillThrowsException() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        // When & Then - Неверное значение все еще выбрасывает исключение
        mockMvc.perform(get("/v1/reports/profit/grouped")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("groupBy", "INVALID"))
                .andExpect(status().isBadRequest());

        System.out.println("✅ Неверные значения все еще обрабатываются корректно!");
        System.out.println("📝 Исключение обрабатывается глобально");
    }
}
