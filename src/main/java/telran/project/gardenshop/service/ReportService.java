package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    
    /**
     * Получить ТОП-N товаров по продажам
     * @param limit количество товаров в отчете
     * @return список товаров с информацией о продажах
     */
    List<ProductReportDto> getTopProductsBySales(int limit);
    
    /**
     * Получить отчет по прибыли за период
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return отчет по прибыли
     */
    ProfitReportDto getProfitReport(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Получить список заказов в статусе "Ожидает оплаты" более N дней
     * @param daysOlder количество дней
     * @return список заказов с информацией о товарах
     */
    List<PendingPaymentReportDto> getPendingPaymentOrders(int daysOlder);
} 