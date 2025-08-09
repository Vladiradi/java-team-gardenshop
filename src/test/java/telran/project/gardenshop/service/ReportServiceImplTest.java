package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.OrderItemRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        // Создаем тестовые данные
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

        when(orderRepository.findAllByStatus(OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList(order1));

        List<ProductReportDto> result = reportService.getTopProductsBySales(5);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        ProductReportDto firstProduct = result.get(0);
        assertEquals(product1.getId(), firstProduct.getProductId());
        assertEquals(product1.getName(), firstProduct.getProductName());
        assertEquals(2L, firstProduct.getTotalQuantitySold());
        assertEquals(BigDecimal.valueOf(200), firstProduct.getTotalRevenue());
        assertEquals(1, firstProduct.getRank());
        
        ProductReportDto secondProduct = result.get(1);
        assertEquals(product2.getId(), secondProduct.getProductId());
        assertEquals(1L, secondProduct.getTotalQuantitySold());
        assertEquals(BigDecimal.valueOf(200), secondProduct.getTotalRevenue());
        assertEquals(2, secondProduct.getRank());
    }

    @Test
    void getProfitReport() {

        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.DELIVERED))
                .thenReturn(Collections.singletonList(order1));

        ProfitReportDto result = reportService.getProfitReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(BigDecimal.valueOf(400).doubleValue(), result.getTotalRevenue().doubleValue());
        assertEquals(BigDecimal.valueOf(240).doubleValue(), result.getTotalCost().doubleValue()); // 60% от выручки
        assertEquals(BigDecimal.valueOf(160).doubleValue(), result.getTotalProfit().doubleValue());
        assertEquals(1L, result.getTotalOrders());
        assertEquals(3L, result.getTotalItemsSold());
    }

    @Test
    void getPendingPaymentOrders() {

        when(orderRepository.findAllByStatusAndCreatedAtBefore(eq(OrderStatus.NEW), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(order2));

        List<PendingPaymentReportDto> result = reportService.getPendingPaymentOrders(7);

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