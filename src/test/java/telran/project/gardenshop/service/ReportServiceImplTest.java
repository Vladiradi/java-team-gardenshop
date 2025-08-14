package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.ProductReportDto;
import telran.project.gardenshop.dto.ProfitReportDto;
import telran.project.gardenshop.dto.GroupedProfitReportDto;
import telran.project.gardenshop.dto.PendingPaymentReportDto;
import telran.project.gardenshop.dto.CancelledProductReportDto;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Product product1;
    private Product product2;
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private Order deliveredOrder;
    private Order cancelledOrder;

    @BeforeEach
    void setUp() {
        // Инициализация товаров
        product1 = Product.builder()
                .id(1L)
                .name("Rose")
                .price(new BigDecimal("10"))
                .imageUrl("rose.jpg")
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Tulip")
                .price(new BigDecimal("5"))
                .imageUrl("tulip.jpg")
                .build();

        // Инициализация позиций заказов
        orderItem1 = OrderItem.builder()
                .id(1L)
                .product(product1)
                .quantity(3)
                .price(product1.getPrice())
                .build();

        orderItem2 = OrderItem.builder()
                .id(2L)
                .product(product2)
                .quantity(2)
                .price(product2.getPrice())
                .build();

        // Создаем заказ со статусом DELIVERED
        deliveredOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.DELIVERED)
                .createdAt(LocalDateTime.now().minusDays(1))
                .items(List.of(orderItem1, orderItem2))
                .user(User.builder()
                        .id(1L)
                        .email("user@example.com")
                        .fullName("John Doe")
                        .build())
                .build();

        // Создаем заказ со статусом CANCELLED
        cancelledOrder = Order.builder()
                .id(2L)
                .status(OrderStatus.CANCELLED)
                .createdAt(LocalDateTime.now().minusDays(2))
                .items(List.of(orderItem1))
                .user(User.builder()
                        .id(2L)
                        .email("jane@example.com")
                        .fullName("Jane Doe")
                        .build())
                .build();
    }

    @Test
    void testGetTopProductsBySales() {
        when(orderService.getOrdersByStatus(OrderStatus.DELIVERED))
                .thenReturn(List.of(deliveredOrder));

        var result = reportService.getTopProductsBySales(5);

        assertEquals(2, result.size());
        assertEquals(product1.getId(), result.get(0).getProductId());
        assertEquals(3, result.get(0).getTotalQuantitySold());
        assertEquals(product2.getId(), result.get(1).getProductId());
        assertEquals(2, result.get(1).getTotalQuantitySold());
    }

    @Test
    void testGetTopProductsByCancellations() {
        when(orderService.getOrdersByStatus(OrderStatus.CANCELLED))
                .thenReturn(List.of(cancelledOrder));

        var result = reportService.getTopProductsByCancellations(5);

        assertEquals(1, result.size());
        assertEquals(product1.getId(), result.get(0).getProductId());
        assertEquals(3, result.get(0).getTotalQuantityCancelled());
    }

    @Test
    void testGetPendingPaymentOrders() {
        // Создаем товар и заказ для теста PendingPayment
        OrderItem pendingItem = OrderItem.builder()
                .id(3L)
                .product(Product.builder().id(3L).name("Plant").price(BigDecimal.valueOf(10)).imageUrl("plant.jpg").build())
                .quantity(5)
                .price(BigDecimal.valueOf(10))
                .build();

        Order pendingOrder = Order.builder()
                .id(3L)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now().minusDays(5))
                .items(List.of(pendingItem))
                .user(User.builder().id(3L).email("bob@example.com").fullName("Bob Smith").build())
                .build();

        // Мокируем вызов
        when(orderService.getOrdersByStatusAndCreatedBefore(eq(OrderStatus.NEW), any(LocalDateTime.class)))
                .thenReturn(List.of(pendingOrder));

        List<PendingPaymentReportDto> result = reportService.getPendingPaymentOrders(3);

        assertEquals(1, result.size(), "Должен быть один ожидающий заказ");
        PendingPaymentReportDto dto = result.get(0);
        assertEquals(pendingOrder.getId(), dto.getOrderId());
        assertEquals(1, dto.getItems().size(), "В заказе должен быть один товар");
        assertEquals(pendingItem.getQuantity(), dto.getItems().get(0).getQuantity());
    }

    @Test
    void testGetGroupedProfitReportEmpty() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(orderService.getOrdersByDateRangeAndStatus(start, end, OrderStatus.DELIVERED))
                .thenReturn(Collections.emptyList());

        GroupedProfitReportDto report = reportService.getGroupedProfitReport(start, end, GroupByPeriod.DAY);

        assertEquals(0, report.getGroupedData().size());
        assertEquals(BigDecimal.ZERO, report.getTotalRevenue());
        assertEquals(0L, report.getTotalOrders());
    }
}
