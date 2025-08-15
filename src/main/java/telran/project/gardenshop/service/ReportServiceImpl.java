package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.enums.ProductReportType;
import telran.project.gardenshop.repository.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;

    @Override
    public ProfitReportDto getProfitReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> ordersInPeriod = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startDate, endDate, OrderStatus.DELIVERED);

        BigDecimal totalRevenue = ordersInPeriod.stream()
                .flatMap(order -> order.getItems().stream())
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = totalRevenue.multiply(BigDecimal.valueOf(0.6));
        BigDecimal totalProfit = totalRevenue.subtract(totalCost);
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        long totalOrders = ordersInPeriod.size();
        long totalItemsSold = ordersInPeriod.stream()
                .flatMapToLong(order -> order.getItems().stream().mapToLong(OrderItem::getQuantity))
                .sum();

        return ProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue)
                .totalCost(totalCost)
                .totalProfit(totalProfit)
                .profitMargin(profitMargin)
                .totalOrders(totalOrders)
                .totalItemsSold(totalItemsSold)
                .build();
    }

    @Override
    public GroupedProfitReportDto getGroupedProfitReport(LocalDateTime startDate, LocalDateTime endDate, GroupByPeriod groupBy) {
        List<Order> ordersInPeriod = orderRepository.findAllByCreatedAtBetweenAndStatus(
                startDate, endDate, OrderStatus.DELIVERED);

        if (ordersInPeriod.isEmpty()) {
            return createEmptyGroupedReport(startDate, endDate, groupBy);
        }

        Map<String, List<Order>> groupedOrders = groupOrdersByTimePeriod(ordersInPeriod, groupBy);

        List<GroupedProfitReportDto.GroupedProfitData> groupedData = groupedOrders.entrySet().stream()
                .map(entry -> calculateGroupedProfitData(entry.getKey(), entry.getValue(), groupBy))
                .sorted((g1, g2) -> g1.getPeriodStart().compareTo(g2.getPeriodStart()))
                .collect(Collectors.toList());

        BigDecimal totalRevenue = groupedData.stream()
                .map(GroupedProfitReportDto.GroupedProfitData::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = totalRevenue.multiply(BigDecimal.valueOf(0.6));
        BigDecimal totalProfit = totalRevenue.subtract(totalCost);
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        long totalOrders = ordersInPeriod.size();
        long totalItemsSold = ordersInPeriod.stream()
                .flatMapToLong(order -> order.getItems().stream().mapToLong(OrderItem::getQuantity))
                .sum();

        return GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy.name())
                .groupedData(groupedData)
                .totalRevenue(totalRevenue)
                .totalCost(totalCost)
                .totalProfit(totalProfit)
                .profitMargin(profitMargin)
                .totalOrders(totalOrders)
                .totalItemsSold(totalItemsSold)
                .build();
    }

    private Map<String, List<Order>> groupOrdersByTimePeriod(List<Order> orders, GroupByPeriod groupBy) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> getPeriodKey(order.getCreatedAt(), groupBy)));
    }

    private String getPeriodKey(LocalDateTime dateTime, GroupByPeriod groupBy) {
        switch (groupBy) {
            case HOUR:
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
            case WEEK:
                LocalDate date = dateTime.toLocalDate();
                LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                return "Week " + weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case MONTH:
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            case DAY:
            default:
                return dateTime.toLocalDate().toString();
        }
    }

    private GroupedProfitReportDto.GroupedProfitData calculateGroupedProfitData(String periodKey, List<Order> orders, GroupByPeriod groupBy) {
        BigDecimal revenue = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cost = revenue.multiply(BigDecimal.valueOf(0.6));
        BigDecimal profit = revenue.subtract(cost);
        BigDecimal profitMargin = revenue.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        long orderCount = orders.size();
        long itemsSold = orders.stream()
                .flatMapToLong(order -> order.getItems().stream().mapToLong(OrderItem::getQuantity))
                .sum();

        LocalDateTime periodStart = getPeriodStart(periodKey, groupBy);
        LocalDateTime periodEnd = getPeriodEnd(periodKey, groupBy);

        return GroupedProfitReportDto.GroupedProfitData.builder()
                .periodLabel(periodKey)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .revenue(revenue)
                .cost(cost)
                .profit(profit)
                .profitMargin(profitMargin)
                .orderCount(orderCount)
                .itemsSold(itemsSold)
                .build();
    }

    private LocalDateTime getPeriodStart(String periodKey, GroupByPeriod groupBy) {
        return switch (groupBy) {
            case HOUR -> LocalDateTime.parse(periodKey + ":00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            case DAY -> LocalDate.parse(periodKey).atStartOfDay();
            case WEEK -> {
                String datePart = periodKey.substring(5);
                yield LocalDate.parse(datePart).atStartOfDay();
            }
            case MONTH -> LocalDate.parse(periodKey + "-01").atStartOfDay();
        };
    }

    private LocalDateTime getPeriodEnd(String periodKey, GroupByPeriod groupBy) {
        return switch (groupBy) {
            case HOUR -> LocalDateTime.parse(periodKey + ":00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusHours(1);
            case DAY -> LocalDate.parse(periodKey).atTime(23, 59, 59);
            case WEEK -> {
                String datePart = periodKey.substring(5);
                LocalDate weekStart = LocalDate.parse(datePart);
                yield weekStart.plusDays(6).atTime(23, 59, 59);
            }
            case MONTH -> {
                LocalDate monthStart = LocalDate.parse(periodKey + "-01");
                yield monthStart.plusMonths(1).minusDays(1).atTime(23, 59, 59);
            }
        };
    }

    private GroupedProfitReportDto createEmptyGroupedReport(LocalDateTime startDate, LocalDateTime endDate, GroupByPeriod groupBy) {
        return GroupedProfitReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy.name())
                .groupedData(new ArrayList<>())
                .totalRevenue(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .totalOrders(0L)
                .totalItemsSold(0L)
                .build();
    }

    @Override
    public List<PendingPaymentReportDto> getPendingPaymentOrders(int daysOlder) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOlder);

        List<Order> pendingOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                OrderStatus.NEW, cutoffDate);

        return pendingOrders.stream()
                .map(order -> {
                    long daysPending = ChronoUnit.DAYS.between(order.getCreatedAt(), LocalDateTime.now());

                    BigDecimal orderTotal = order.getItems().stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<OrderItemResponseDto> items = order.getItems().stream()
                            .map(item -> OrderItemResponseDto.builder()
                                    .id(item.getId())
                                    .productId(item.getProduct().getId())
                                    .productName(item.getProduct().getName())
                                    .productImageUrl(item.getProduct().getImageUrl())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice().doubleValue())
                                    .build())
                            .collect(Collectors.toList());

                    return PendingPaymentReportDto.builder()
                            .orderId(order.getId())
                            .userId(order.getUser().getId())
                            .userEmail(order.getUser().getEmail())
                            .userFullName(order.getUser().getFullName())
                            .orderCreatedAt(order.getCreatedAt())
                            .daysPending(daysPending)
                            .orderTotal(orderTotal)
                            .items(items)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductReportDto> getTopProductsByType(ProductReportType reportType, int limit) {
        OrderStatus orderStatus = reportType == ProductReportType.SALES ?
                OrderStatus.DELIVERED : OrderStatus.CANCELLED;

        List<Order> orders = orderRepository.findAllByStatus(orderStatus);
        Map<Long, ProductReportDto> productStats = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                Long productId = product.getId();

                ProductReportDto stats = productStats.computeIfAbsent(productId,
                        k -> ProductReportDto.builder()
                                .productId(productId)
                                .productName(product.getName())
                                .productImageUrl(product.getImageUrl())
                                .productPrice(product.getPrice())
                                .totalQuantity(0L)
                                .totalRevenue(BigDecimal.ZERO)
                                .reportType(reportType)
                                .build());

                stats.setTotalQuantity(stats.getTotalQuantity() + item.getQuantity());

                if (reportType == ProductReportType.SALES) {
                    stats.setTotalRevenue(stats.getTotalRevenue().add(item.getPrice()));
                }
            }
        }

        return productStats.values().stream()
                .sorted(Comparator.comparing(ProductReportDto::getTotalQuantity).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
