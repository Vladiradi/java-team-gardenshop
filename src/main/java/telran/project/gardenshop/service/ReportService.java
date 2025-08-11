package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    List<ProductReportDto> getTopProductsBySales(int limit);

    ProfitReportDto getProfitReport(LocalDateTime startDate, LocalDateTime endDate);

    GroupedProfitReportDto getGroupedProfitReport(LocalDateTime startDate, LocalDateTime endDate, String groupBy);

    List<PendingPaymentReportDto> getPendingPaymentOrders(int daysOlder);
}
