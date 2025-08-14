package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.enums.ProductReportType;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    List<ProductReportDto> getTopProductsByType(ProductReportType reportType, int limit);

    ProfitReportDto getProfitReport(LocalDateTime startDate, LocalDateTime endDate);

    GroupedProfitReportDto getGroupedProfitReport(LocalDateTime startDate, LocalDateTime endDate, GroupByPeriod groupBy);

    List<PendingPaymentReportDto> getPendingPaymentOrders(int daysOlder);
}
