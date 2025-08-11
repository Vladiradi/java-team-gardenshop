package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.OrderItemRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentService paymentService;

    @Override
    public List<ProductReportDto> getTopProductsBySales(int limit) {

        List<Order> completedOrders = orderRepository.findAllByStatus(OrderStatus.DELIVERED);
        

        Map<Long, ProductReportDto> productStats = completedOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                items -> {
                                    OrderItem firstItem = items.get(0);
                                    long totalQuantity = items.stream()
                                            .mapToLong(OrderItem::getQuantity)
                                            .sum();
                                    BigDecimal totalRevenue = items.stream()
                                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    
                                    return ProductReportDto.builder()
                                            .productId(firstItem.getProduct().getId())
                                            .productName(firstItem.getProduct().getName())
                                            .productImageUrl(firstItem.getProduct().getImageUrl())
                                            .productPrice(firstItem.getProduct().getPrice())
                                            .totalQuantitySold(totalQuantity)
                                            .totalRevenue(totalRevenue)
                                            .build();
                                }
                        )
                ));
        

        return productStats.values().stream()
                .sorted((p1, p2) -> p2.getTotalQuantitySold().compareTo(p1.getTotalQuantitySold()))
                .limit(limit)
                .peek(product -> product.setRank(productStats.values().stream()
                        .sorted((p1, p2) -> p2.getTotalQuantitySold().compareTo(p1.getTotalQuantitySold()))
                        .collect(Collectors.toList())
                        .indexOf(product) + 1))
                .collect(Collectors.toList());
    }

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

        // Group orders by time period
        Map<String, List<Order>> groupedOrders = groupOrdersByTimePeriod(ordersInPeriod, groupBy);
        
        // Calculate grouped data
        List<GroupedProfitReportDto.GroupedProfitData> groupedData = groupedOrders.entrySet().stream()
                .map(entry -> calculateGroupedProfitData(entry.getKey(), entry.getValue(), groupBy))
                .sorted((g1, g2) -> g1.getPeriodStart().compareTo(g2.getPeriodStart()))
                .collect(Collectors.toList());

        // Calculate totals
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
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
            case DAY:
                return dateTime.toLocalDate().toString();
            case WEEK:
                LocalDate date = dateTime.toLocalDate();
                LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                return "Week " + weekStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case MONTH:
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
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
        switch (groupBy) {
            case HOUR:
                return LocalDateTime.parse(periodKey + ":00", 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            case DAY:
                return LocalDate.parse(periodKey).atStartOfDay();
            case WEEK:
                // periodKey format: "Week YYYY-MM-DD"
                String datePart = periodKey.substring(5); // Remove "Week " prefix
                return LocalDate.parse(datePart).atStartOfDay();
            case MONTH:
                return LocalDate.parse(periodKey + "-01").atStartOfDay();
            default:
                return LocalDate.parse(periodKey).atStartOfDay();
        }
    }

    private LocalDateTime getPeriodEnd(String periodKey, GroupByPeriod groupBy) {
        switch (groupBy) {
            case HOUR:
                return LocalDateTime.parse(periodKey + ":00", 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusHours(1);
            case DAY:
                return LocalDate.parse(periodKey).atTime(23, 59, 59);
            case WEEK:
                // periodKey format: "Week YYYY-MM-DD"
                String datePart = periodKey.substring(5); // Remove "Week " prefix
                LocalDate weekStart = LocalDate.parse(datePart);
                return weekStart.plusDays(6).atTime(23, 59, 59);
            case MONTH:
                LocalDate monthStart = LocalDate.parse(periodKey + "-01");
                return monthStart.plusMonths(1).minusDays(1).atTime(23, 59, 59);
            default:
                return LocalDate.parse(periodKey).atTime(23, 59, 59);
        }
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
} 