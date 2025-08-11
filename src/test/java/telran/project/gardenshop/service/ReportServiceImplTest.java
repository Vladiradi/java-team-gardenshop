package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.OrderItemRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Order order1, order2;
    private Product product1, product2;
    private OrderItem item1, item2, item3;

    @BeforeEach
    void setUp() {
        // Create test data
        Category category = Category.builder().id(1L).name("Test Category").build();

        product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .imageUrl("image1.jpg")
                .category(category)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .price(BigDecimal.valueOf(200))
                .imageUrl("image2.jpg")
                .category(category)
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .build();

        order1 = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.DELIVERED)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        order2 = Order.builder()
                .id(2L)
                .user(user)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        item1 = OrderItem.builder()
                .id(1L)
                .order(order1)
                .product(product1)
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .build();

        item2 = OrderItem.builder()
                .id(2L)
                .order(order1)
                .product(product2)
                .quantity(1)
                .price(BigDecimal.valueOf(200))
                .build();

        item3 = OrderItem.builder()
                .id(3L)
                .order(order2)
                .product(product1)
                .quantity(3)
                .price(BigDecimal.valueOf(100))
                .build();

        order1.setItems(Arrays.asList(item1, item2));
        order2.setItems(Arrays.asList(item3));
    }

    @Test
    void getTopProductsBySales() {
        // Given
        when(orderRepository.findAllByStatus(OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        // When
        List<ProductReportDto> result = reportService.getTopProductsBySales(10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        ProductReportDto product1Report = result.stream()
                .filter(p -> p.getProductId().equals(1L))
                .findFirst()
                .orElse(null);
        assertNotNull(product1Report);
        assertEquals("Product 1", product1Report.getProductName());
        assertEquals(2L, product1Report.getTotalQuantitySold());
        assertEquals(BigDecimal.valueOf(200), product1Report.getTotalRevenue());
    }

    @Test
    void getProfitReport() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        // When
        ProfitReportDto result = reportService.getProfitReport(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(BigDecimal.valueOf(400).doubleValue(), result.getTotalRevenue().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(240).doubleValue(), result.getTotalCost().doubleValue(), 0.001); // 60% of revenue
        assertEquals(BigDecimal.valueOf(160).doubleValue(), result.getTotalProfit().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(40.0).doubleValue(), result.getProfitMargin().doubleValue(), 0.001); // 40% margin
        assertEquals(1L, result.getTotalOrders());
        assertEquals(3L, result.getTotalItemsSold());
    }

    @Test
    void getGroupedProfitReport_WithDayGrouping() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        GroupByPeriod groupBy = GroupByPeriod.DAY;

        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        // When
        GroupedProfitReportDto result = reportService.getGroupedProfitReport(startDate, endDate, groupBy);

        // Then
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(groupBy.name(), result.getGroupBy());
        assertEquals(BigDecimal.valueOf(400).doubleValue(), result.getTotalRevenue().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(240).doubleValue(), result.getTotalCost().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(160).doubleValue(), result.getTotalProfit().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(40.0).doubleValue(), result.getProfitMargin().doubleValue(), 0.001);
        assertEquals(1L, result.getTotalOrders());
        assertEquals(3L, result.getTotalItemsSold());

        assertNotNull(result.getGroupedData());
        assertEquals(1, result.getGroupedData().size());

        GroupedProfitReportDto.GroupedProfitData groupedData = result.getGroupedData().get(0);
        assertNotNull(groupedData.getPeriodLabel());
        assertNotNull(groupedData.getPeriodStart());
        assertNotNull(groupedData.getPeriodEnd());
        assertEquals(BigDecimal.valueOf(400).doubleValue(), groupedData.getRevenue().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(240).doubleValue(), groupedData.getCost().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(160).doubleValue(), groupedData.getProfit().doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(40.0).doubleValue(), groupedData.getProfitMargin().doubleValue(), 0.001);
        assertEquals(1L, groupedData.getOrderCount());
        assertEquals(3L, groupedData.getItemsSold());
    }

    @Test
    void getGroupedProfitReport_WithHourGrouping() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        GroupByPeriod groupBy = GroupByPeriod.HOUR;

        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        // When
        GroupedProfitReportDto result = reportService.getGroupedProfitReport(startDate, endDate, groupBy);

        // Then
        assertNotNull(result);
        assertEquals(groupBy.name(), result.getGroupBy());
        assertNotNull(result.getGroupedData());
        assertEquals(1, result.getGroupedData().size());
    }

    @Test
    void getGroupedProfitReport_WithWeekGrouping_ShouldReturnGroupedData() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(2);
        LocalDateTime endDate = LocalDateTime.now();
        GroupByPeriod groupBy = GroupByPeriod.WEEK;
        
        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        // When
        GroupedProfitReportDto result = reportService.getGroupedProfitReport(startDate, endDate, groupBy);

        // Then
        assertNotNull(result);
        assertEquals(groupBy.name(), result.getGroupBy());
        assertNotNull(result.getGroupedData());
        assertEquals(1, result.getGroupedData().size());
    }

    @Test
    void getGroupedProfitReport_WithMonthGrouping_ShouldReturnGroupedData() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusMonths(3);
        LocalDateTime endDate = LocalDateTime.now();
        GroupByPeriod groupBy = GroupByPeriod.MONTH;
        
        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        // When
        GroupedProfitReportDto result = reportService.getGroupedProfitReport(startDate, endDate, groupBy);

        // Then
        assertNotNull(result);
        assertEquals(groupBy.name(), result.getGroupBy());
        assertNotNull(result.getGroupedData());
        assertEquals(1, result.getGroupedData().size());
    }

    @Test
    void getGroupedProfitReport_WithNoOrders_ShouldReturnEmptyReport() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        GroupByPeriod groupBy = GroupByPeriod.DAY;
        
        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Collections.emptyList());

        // When
        GroupedProfitReportDto result = reportService.getGroupedProfitReport(startDate, endDate, groupBy);

        // Then
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(groupBy.name(), result.getGroupBy());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals(BigDecimal.ZERO, result.getTotalProfit());
        assertEquals(BigDecimal.ZERO, result.getProfitMargin());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(0L, result.getTotalItemsSold());
        assertTrue(result.getGroupedData().isEmpty());
    }

    @Test
    void getGroupedProfitReport_WithMultipleOrders() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 3, 23, 59, 59);
        GroupByPeriod groupBy = GroupByPeriod.DAY;

        // Create orders for different days with fixed timestamps
        Order orderDay1 = Order.builder()
                .id(3L)
                .user(order1.getUser())
                .status(OrderStatus.DELIVERED)
                .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0)) // January 1st
                .items(Arrays.asList(item1))
                .build();

        Order orderDay2 = Order.builder()
                .id(4L)
                .user(order1.getUser())
                .status(OrderStatus.DELIVERED)
                .createdAt(LocalDateTime.of(2024, 1, 2, 12, 0)) // January 2nd
                .items(Arrays.asList(item2))
                .build();

        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(orderDay1, orderDay2));

        // When
        GroupedProfitReportDto result = reportService.getGroupedProfitReport(startDate, endDate, groupBy);

        // Then
        assertNotNull(result);
        assertEquals(groupBy.name(), result.getGroupBy());
        assertNotNull(result.getGroupedData());
        assertEquals(2, result.getGroupedData().size());

        // Verify totals
        assertEquals(BigDecimal.valueOf(400).doubleValue(), result.getTotalRevenue().doubleValue()); // 200 + 100
        assertEquals(BigDecimal.valueOf(240).doubleValue(), result.getTotalCost().doubleValue()); // 60% of 300
        assertEquals(BigDecimal.valueOf(160).doubleValue(), result.getTotalProfit().doubleValue()); // 300 - 180
        assertEquals(2L, result.getTotalOrders());
        assertEquals(3L, result.getTotalItemsSold());

        // Verify individual period data
        List<GroupedProfitReportDto.GroupedProfitData> sortedData = result.getGroupedData().stream()
                .sorted((g1, g2) -> g1.getPeriodStart().compareTo(g2.getPeriodStart()))
                .collect(Collectors.toList());

        // First day (January 1st)
        GroupedProfitReportDto.GroupedProfitData day1Data = sortedData.get(0);
        assertEquals("2024-01-01", day1Data.getPeriodLabel());
        assertEquals(BigDecimal.valueOf(200), day1Data.getRevenue()); // item1: 2 * 100

        // Second day (January 2nd)
        GroupedProfitReportDto.GroupedProfitData day2Data = sortedData.get(1);
        assertEquals("2024-01-02", day2Data.getPeriodLabel());
        assertEquals(BigDecimal.valueOf(200), day2Data.getRevenue());
    }

    @Test
    void getPendingPaymentOrders() {
        // Given
        when(orderRepository.findAllByStatusAndCreatedAtBefore(eq(OrderStatus.NEW), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(order2));

        // When
        List<PendingPaymentReportDto> result = reportService.getPendingPaymentOrders(7);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        PendingPaymentReportDto pendingOrder = result.get(0);
        assertEquals(order2.getId(), pendingOrder.getOrderId());
        assertEquals(order2.getUser().getId(), pendingOrder.getUserId());
        assertEquals(order2.getUser().getEmail(), pendingOrder.getUserEmail());
        assertEquals(order2.getUser().getFullName(), pendingOrder.getUserFullName());
        assertEquals(order2.getCreatedAt(), pendingOrder.getOrderCreatedAt());
        assertTrue(pendingOrder.getDaysPending() >= 7);
        assertEquals(BigDecimal.valueOf(300), pendingOrder.getOrderTotal());
        assertEquals(1, pendingOrder.getItems().size());
    }
}
