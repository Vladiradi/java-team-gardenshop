package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.OrderItemRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .totalOrders(totalOrders)
                .totalItemsSold(totalItemsSold)
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