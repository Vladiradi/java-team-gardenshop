package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderService orderService;

    // === Top products by sales ===
    @Override
    public List<ProductReportDto> getTopProductsBySales(int limit) {
        List<Order> deliveredOrders = orderService.getOrdersByStatus(OrderStatus.DELIVERED);

        return deliveredOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getProduct().getId()))
                .values().stream()
                .map(items -> {
                    Product product = items.get(0).getProduct();
                    long totalQuantity = items.stream().mapToLong(OrderItem::getQuantity).sum();
                    BigDecimal totalRevenue = items.stream()
                            .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return ProductReportDto.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .productImageUrl(product.getImageUrl())
                            .productPrice(product.getPrice())
                            .totalQuantitySold(totalQuantity)
                            .totalRevenue(totalRevenue)
                            .build();
                })
                .sorted(Comparator.comparing(ProductReportDto::getTotalQuantitySold).reversed())
                .limit(limit)
                .toList();
    }

    // === Top products by cancellations ===
    @Override
    public List<CancelledProductReportDto> getTopProductsByCancellations(int limit) {
        List<Order> cancelledOrders = orderService.getOrdersByStatus(OrderStatus.CANCELLED);

        Map<Long, List<OrderItem>> itemsByProduct = cancelledOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(i -> i.getProduct().getId()));

        return itemsByProduct.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    List<OrderItem> items = entry.getValue();
                    Product product = items.get(0).getProduct();
                    long totalQuantityCancelled = items.stream().mapToLong(OrderItem::getQuantity).sum();

                    return CancelledProductReportDto.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .productImageUrl(product.getImageUrl())
                            .productPrice(product.getPrice())
                            .totalQuantityCancelled(totalQuantityCancelled)
                            .build();
                })
                .sorted(Comparator.comparing(CancelledProductReportDto::getTotalQuantityCancelled).reversed())
                .limit(limit)
                .toList();
    }

    // === Pending payment orders ===
    @Override
    public List<PendingPaymentReportDto> getPendingPaymentOrders(int daysOlder) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOlder);
        List<Order> pendingOrders = orderService.getOrdersByStatusAndCreatedBefore(OrderStatus.NEW, cutoffDate);

        return pendingOrders.stream()
                .map(order -> {
                    BigDecimal orderTotal = order.getItems().stream()
                            .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<OrderItemResponseDto> items = order.getItems().stream()
                            .map(i -> OrderItemResponseDto.builder()
                                    .id(i.getId())
                                    .productId(i.getProduct().getId())
                                    .productName(i.getProduct().getName())
                                    .productImageUrl(i.getProduct().getImageUrl())
                                    .quantity(i.getQuantity())
                                    .price(i.getPrice().doubleValue())
                                    .build())
                            .toList();

                    return PendingPaymentReportDto.builder()
                            .orderId(order.getId())
                            .userId(order.getUser().getId())
                            .userEmail(order.getUser().getEmail())
                            .userFullName(order.getUser().getFullName())
                            .orderCreatedAt(order.getCreatedAt())
                            .daysPending(ChronoUnit.DAYS.between(order.getCreatedAt(), LocalDateTime.now()))
                            .orderTotal(orderTotal)
                            .items(items)
                            .build();
                })
                .toList();
    }

    // === Profit report ===
    @Override
    public ProfitReportDto getProfitReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderService.getOrdersByDateRangeAndStatus(startDate, endDate, OrderStatus.DELIVERED);
        ProfitMetrics metrics = calculateMetrics(orders);

        return ProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(metrics.revenue())
                .totalCost(metrics.cost())
                .totalProfit(metrics.profit())
                .profitMargin(metrics.profitMargin())
                .totalOrders(metrics.ordersCount())
                .totalItemsSold(metrics.itemsSold())
                .build();
    }

    // === Grouped profit report ===
    @Override
    public GroupedProfitReportDto getGroupedProfitReport(LocalDateTime startDate, LocalDateTime endDate, GroupByPeriod groupBy) {
        List<Order> orders = orderService.getOrdersByDateRangeAndStatus(startDate, endDate, OrderStatus.DELIVERED);
        if (orders.isEmpty()) return createEmptyGroupedReport(startDate, endDate, groupBy);

        Map<String, List<Order>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(o -> getPeriodKey(o.getCreatedAt(), groupBy)));

        List<GroupedProfitReportDto.GroupedProfitData> groupedData = groupedOrders.entrySet().stream()
                .map(entry -> calculateGroupedProfitData(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(GroupedProfitReportDto.GroupedProfitData::getPeriodStart))
                .toList();

        ProfitMetrics totals = calculateMetrics(orders);

        return GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy.name())
                .groupedData(groupedData)
                .totalRevenue(totals.revenue())
                .totalCost(totals.cost())
                .totalProfit(totals.profit())
                .profitMargin(totals.profitMargin())
                .totalOrders(totals.ordersCount())
                .totalItemsSold(totals.itemsSold())
                .build();
    }

    // === Helper methods ===

    private String getPeriodKey(LocalDateTime dateTime, GroupByPeriod groupBy) {
        return switch (groupBy) {
            case HOUR -> dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
            case WEEK -> {
                LocalDate weekStart = dateTime.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                yield "Week " + weekStart.toString();
            }
            case MONTH -> dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            default -> dateTime.toLocalDate().toString();
        };
    }

    private GroupedProfitReportDto.GroupedProfitData calculateGroupedProfitData(String periodKey, List<Order> orders) {
        ProfitMetrics metrics = calculateMetrics(orders);
        LocalDateTime periodStart = orders.stream().map(Order::getCreatedAt).min(LocalDateTime::compareTo).orElse(LocalDateTime.now());

        return GroupedProfitReportDto.GroupedProfitData.builder()
                .periodLabel(periodKey)
                .periodStart(periodStart)
                .revenue(metrics.revenue())
                .cost(metrics.cost())
                .profit(metrics.profit())
                .profitMargin(metrics.profitMargin())
                .orderCount(metrics.ordersCount())
                .itemsSold(metrics.itemsSold())
                .build();
    }

    private ProfitMetrics calculateMetrics(List<Order> orders) {
        BigDecimal revenue = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cost = revenue.multiply(BigDecimal.valueOf(0.6));
        BigDecimal profit = revenue.subtract(cost);
        BigDecimal profitMargin = revenue.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        long itemsSold = orders.stream()
                .flatMapToLong(o -> o.getItems().stream().mapToLong(OrderItem::getQuantity))
                .sum();

        return new ProfitMetrics(revenue, cost, profit, profitMargin, itemsSold, (long) orders.size());
    }

    private GroupedProfitReportDto createEmptyGroupedReport(LocalDateTime startDate, LocalDateTime endDate, GroupByPeriod groupBy) {
        return GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy.name())
                .groupedData(Collections.emptyList())
                .totalRevenue(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .totalOrders(0L)
                .totalItemsSold(0L)
                .build();
    }

    private record ProfitMetrics(BigDecimal revenue, BigDecimal cost, BigDecimal profit, BigDecimal profitMargin, long itemsSold, long ordersCount) {}
}
