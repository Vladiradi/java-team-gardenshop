package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.service.ReportService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-products")
    @Operation(summary = "Get top N products by sales")
    public ResponseEntity<List<ProductReportDto>> getTopProductsBySales(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductReportDto> topProducts = reportService.getTopProductsBySales(limit);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/profit")
    @Operation(summary = "Get profit report for period")
    public ResponseEntity<ProfitReportDto> getProfitReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        ProfitReportDto profitReport = reportService.getProfitReport(startDate, endDate);
        return ResponseEntity.ok(profitReport);
    }

    @GetMapping("/pending-payments")
    @Operation(summary = "Get orders pending payment for more than N days")
    public ResponseEntity<List<PendingPaymentReportDto>> getPendingPaymentOrders(
            @RequestParam(defaultValue = "7") int daysOlder) {
        List<PendingPaymentReportDto> pendingOrders = reportService.getPendingPaymentOrders(daysOlder);
        return ResponseEntity.ok(pendingOrders);
    }
} 