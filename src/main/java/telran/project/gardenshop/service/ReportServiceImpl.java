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

        private final OrderService orderService;

        private static final BigDecimal COST_MULTIPLIER = BigDecimal.valueOf(0.6);
        private static final int SCALE = 4;

        @Override
        public ProfitReportDto getProfitReport(LocalDateTime startDate, LocalDateTime endDate) {
                List<Order> ordersInPeriod = orderService.getAllByCreatedAtBetweenAndStatus(
                                startDate, endDate, OrderStatus.DELIVERED);

                if (ordersInPeriod.isEmpty()) {
                        return createEmptyProfitReport(startDate, endDate);
                }

                return buildProfitReport(startDate, endDate, ordersInPeriod);
        }

        @Override
        public GroupedProfitReportDto getGroupedProfitReport(LocalDateTime startDate, LocalDateTime endDate,
                        GroupByPeriod groupBy) {
                List<Order> ordersInPeriod = orderService.getAllByCreatedAtBetweenAndStatus(
                                startDate, endDate, OrderStatus.DELIVERED);

                if (ordersInPeriod.isEmpty()) {
                        return createEmptyGroupedReport(startDate, endDate, groupBy);
                }

                Map<String, List<Order>> groupedOrders = groupOrdersByTimePeriod(ordersInPeriod, groupBy);

                List<GroupedProfitReportDto.GroupedProfitData> groupedData = groupedOrders.entrySet().stream()
                                .map(entry -> calculateGroupedProfitData(entry.getKey(), entry.getValue(), groupBy))
                                .sorted(Comparator.comparing(GroupedProfitReportDto.GroupedProfitData::getPeriodStart))
                                .collect(Collectors.toList());

                return buildGroupedProfitReport(startDate, endDate, groupBy, groupedData, ordersInPeriod);
        }

        @Override
        public List<PendingPaymentReportDto> getPendingPaymentOrders(int daysOlder) {
                LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOlder);

                List<Order> pendingOrders = orderService.getAllByStatusAndCreatedAtBefore(
                                OrderStatus.NEW, cutoffDate);

                return pendingOrders.stream()
                                .map(this::buildPendingPaymentReportDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<ProductReportDto> getTopProductsByType(ProductReportType reportType, int limit) {
                OrderStatus orderStatus = reportType == ProductReportType.SALES ? OrderStatus.DELIVERED
                                : OrderStatus.CANCELLED;

                List<Order> orders = orderService.getAllByStatus(orderStatus);
                Map<Long, ProductReportDto> productStats = buildProductStats(orders, reportType);

                return productStats.values().stream()
                                .sorted(Comparator.comparing(ProductReportDto::getTotalQuantity).reversed())
                                .limit(limit)
                                .collect(Collectors.toList());
        }

        private ProfitReportDto buildProfitReport(LocalDateTime startDate, LocalDateTime endDate, List<Order> orders) {
                BigDecimal totalRevenue = calculateTotalRevenue(orders);
                ProfitCalculation profitCalc = calculateProfit(totalRevenue);

                long totalOrders = orders.size();
                long totalItemsSold = calculateTotalItemsSold(orders);

                return ProfitReportDto.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .totalRevenue(totalRevenue)
                                .totalCost(profitCalc.cost())
                                .totalProfit(profitCalc.profit())
                                .profitMargin(profitCalc.profitMargin())
                                .totalOrders(totalOrders)
                                .totalItemsSold(totalItemsSold)
                                .build();
        }

        private GroupedProfitReportDto buildGroupedProfitReport(LocalDateTime startDate, LocalDateTime endDate, GroupByPeriod groupBy,
                        List<GroupedProfitReportDto.GroupedProfitData> groupedData, List<Order> orders) {
                BigDecimal totalRevenue = groupedData.stream()
                                .map(GroupedProfitReportDto.GroupedProfitData::getRevenue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                ProfitCalculation profitCalc = calculateProfit(totalRevenue);

                long totalOrders = orders.size();
                long totalItemsSold = calculateTotalItemsSold(orders);

                return GroupedProfitReportDto.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .groupBy(groupBy.name())
                                .groupedData(groupedData)
                                .totalRevenue(totalRevenue)
                                .totalCost(profitCalc.cost())
                                .totalProfit(profitCalc.profit())
                                .profitMargin(profitCalc.profitMargin())
                                .totalOrders(totalOrders)
                                .totalItemsSold(totalItemsSold)
                                .build();
        }

        private BigDecimal calculateTotalRevenue(List<Order> orders) {
                return orders.stream()
                                .flatMap(order -> order.getItems().stream())
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private long calculateTotalItemsSold(List<Order> orders) {
                return orders.stream()
                                .flatMapToLong(order -> order.getItems().stream().mapToLong(OrderItem::getQuantity))
                                .sum();
        }

        private BigDecimal calculateProfitMargin(BigDecimal revenue, BigDecimal profit) {
                return revenue.compareTo(BigDecimal.ZERO) > 0
                                ? profit.divide(revenue, SCALE, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;
        }

        private ProfitCalculation calculateProfit(BigDecimal revenue) {
                BigDecimal cost = revenue.multiply(COST_MULTIPLIER);
                BigDecimal profit = revenue.subtract(cost);
                BigDecimal profitMargin = calculateProfitMargin(revenue, profit);
                return new ProfitCalculation(cost, profit, profitMargin);
        }

        private record ProfitCalculation(BigDecimal cost, BigDecimal profit, BigDecimal profitMargin) {}

        private ProfitReportDto createEmptyProfitReport(LocalDateTime startDate, LocalDateTime endDate) {
                return ProfitReportDto.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .totalRevenue(BigDecimal.ZERO)
                                .totalCost(BigDecimal.ZERO)
                                .totalProfit(BigDecimal.ZERO)
                                .profitMargin(BigDecimal.ZERO)
                                .totalOrders(0L)
                                .totalItemsSold(0L)
                                .build();
        }

        private GroupedProfitReportDto createEmptyGroupedReport(LocalDateTime startDate, LocalDateTime endDate,
                        GroupByPeriod groupBy) {
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

        private Map<String, List<Order>> groupOrdersByTimePeriod(List<Order> orders, GroupByPeriod groupBy) {
                return orders.stream()
                                .collect(Collectors.groupingBy(order -> getPeriodKey(order.getCreatedAt(), groupBy)));
        }

        private String getPeriodKey(LocalDateTime dateTime, GroupByPeriod groupBy) {
                return switch (groupBy) {
                        case HOUR -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
                        case WEEK -> {
                                LocalDate date = dateTime.toLocalDate();
                                LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                                yield "Week " + weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }
                        case MONTH -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        case DAY -> dateTime.toLocalDate().toString();
                };
        }

        private PendingPaymentReportDto buildPendingPaymentReportDto(Order order) {
                long daysPending = ChronoUnit.DAYS.between(order.getCreatedAt(), LocalDateTime.now());

                BigDecimal orderTotal = calculateOrderTotal(order);

                List<OrderItemResponseDto> items = order.getItems().stream()
                                .map(this::buildOrderItemResponseDto)
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
        }

        private BigDecimal calculateOrderTotal(Order order) {
                return order.getItems().stream()
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private OrderItemResponseDto buildOrderItemResponseDto(OrderItem item) {
                return OrderItemResponseDto.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .productImageUrl(item.getProduct().getImageUrl())
                                .quantity(item.getQuantity())
                                .price(item.getPrice().doubleValue())
                                .build();
        }

        private Map<Long, ProductReportDto> buildProductStats(List<Order> orders, ProductReportType reportType) {
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

                return productStats;
        }

        private GroupedProfitReportDto.GroupedProfitData calculateGroupedProfitData(String periodKey,
                        List<Order> orders, GroupByPeriod groupBy) {
                BigDecimal revenue = calculateTotalRevenue(orders);
                ProfitCalculation profitCalc = calculateProfit(revenue);

                long orderCount = orders.size();
                long itemsSold = calculateTotalItemsSold(orders);

                LocalDateTime periodStart = getPeriodStart(periodKey, groupBy);
                LocalDateTime periodEnd = getPeriodEnd(periodKey, groupBy);

                return GroupedProfitReportDto.GroupedProfitData.builder()
                                .periodLabel(periodKey)
                                .periodStart(periodStart)
                                .periodEnd(periodEnd)
                                .revenue(revenue)
                                .cost(profitCalc.cost())
                                .profit(profitCalc.profit())
                                .profitMargin(profitCalc.profitMargin())
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
}
