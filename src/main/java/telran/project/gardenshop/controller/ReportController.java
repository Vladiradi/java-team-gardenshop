package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.dto.CancelledProductReportDto;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.service.ReportService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-products")
    @Operation(summary = "Get top products by sales quantity")
    public ResponseEntity<List<ProductReportDto>> getTopProductsBySales(
            @RequestParam(defaultValue = "5")
            @Parameter(description = "Number of top products to return",
                      schema = @Schema(minimum = "1", defaultValue = "5"))
            int limit) {
        List<ProductReportDto> topProducts = reportService.getTopProductsBySales(limit);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/top-cancelled-products")
    @Operation(summary = "Get top products by cancellation frequency")
    public ResponseEntity<List<CancelledProductReportDto>> getTopCancelledProducts(
            @RequestParam(defaultValue = "5")
            @Parameter(description = "Number of top cancelled products to return",
                      schema = @Schema(minimum = "1", defaultValue = "5"))
            int limit) {
        List<CancelledProductReportDto> topCancelledProducts = reportService.getTopProductsByCancellations(limit);
        return ResponseEntity.ok(topCancelledProducts);
    }

    @GetMapping("/profit")
    @Operation(summary = "Get profit report for period")
    public ResponseEntity<ProfitReportDto> getProfitReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        ProfitReportDto profitReport = reportService.getProfitReport(startDate, endDate);
        return ResponseEntity.ok(profitReport);
    }

    @GetMapping("/profit/grouped")
    @Operation(summary = "Get profit report grouped by time period (HOUR, DAY, WEEK, MONTH)")
    public ResponseEntity<GroupedProfitReportDto> getGroupedProfitReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Start date for the report period (ISO format: YYYY-MM-DDTHH:mm:ss)",
                      example = "2024-01-01T00:00:00",
                      schema = @Schema(type = "string", format = "date-time"))
            LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "End date for the report period (ISO format: YYYY-MM-DDTHH:mm:ss)",
                      example = "2024-12-31T23:59:59",
                      schema = @Schema(type = "string", format = "date-time"))
            LocalDateTime endDate,
            @RequestParam(defaultValue = "DAY")
            @Parameter(description = "Grouping period",
                      schema = @Schema(allowableValues = {"HOUR", "DAY", "WEEK", "MONTH"},
                                     defaultValue = "DAY"))
            String groupBy) {
        GroupByPeriod groupByPeriod = GroupByPeriod.fromString(groupBy);
        if (groupByPeriod == null) {
            return ResponseEntity.badRequest().build();
        }
        GroupedProfitReportDto groupedReport = reportService.getGroupedProfitReport(startDate, endDate, groupByPeriod);
        return ResponseEntity.ok(groupedReport);
    }

    @GetMapping("/pending-payments")
    @Operation(summary = "Get orders pending payment for more than N days")
    public ResponseEntity<List<PendingPaymentReportDto>> getPendingPaymentOrders(
            @RequestParam(defaultValue = "7") int daysOlder) {
        List<PendingPaymentReportDto> pendingOrders = reportService.getPendingPaymentOrders(daysOlder);
        return ResponseEntity.ok(pendingOrders);
    }
}
