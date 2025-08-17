package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.service.PaymentService;
import telran.project.gardenshop.service.scheduler.SchedulerService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private SchedulerService schedulerService;

    private User testUser;
    private LocalDateTime oldDateTime;
    private LocalDateTime recentDateTime;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).build();
        oldDateTime = LocalDateTime.now().minusDays(1);
        recentDateTime = LocalDateTime.now();
    }

    @Test
    void shouldCancelOnlyOldUnpaidOrders_whenMultipleOrdersExist() {
        Order oldOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .createdAt(oldDateTime)
                .status(OrderStatus.NEW)
                .build();

        Order recentOrder = Order.builder()
                .id(2L)
                .user(testUser)
                .createdAt(recentDateTime)
                .status(OrderStatus.NEW)
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.NEW))
                .thenReturn(List.of(oldOrder, recentOrder));
        when(paymentService.isPaymentStatus(1L, PaymentStatus.UNPAID)).thenReturn(true);

        schedulerService.cancelOrders();

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(OrderStatus.CANCELLED, saved.getStatus());

        verify(paymentService, times(1))
                .updatePaymentStatusByOrderId(1L, PaymentStatus.UNPAID);
        verify(paymentService, never())
                .updatePaymentStatusByOrderId(2L, PaymentStatus.UNPAID);
    }

    @Test
    void shouldSkipOrder_whenPaymentNotPaid() {
        Order order = Order.builder()
                .id(3L)
                .user(testUser)
                .createdAt(oldDateTime)
                .status(OrderStatus.PAID)
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.PAID))
                .thenReturn(List.of(order));
        when(paymentService.isPaymentStatus(3L, PaymentStatus.PAID)).thenReturn(false);

        schedulerService.completePaidOrders();

        verify(orderRepository, never()).save(any());
        verify(paymentService, never()).updatePaymentStatusByOrderId(anyLong(), any());
    }

    @Test
    void shouldCompleteOrder_whenPaymentIsPaid() {
        Order order = Order.builder()
                .id(4L)
                .user(testUser)
                .createdAt(oldDateTime)
                .status(OrderStatus.PAID)
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.PAID))
                .thenReturn(List.of(order));
        when(paymentService.isPaymentStatus(4L, PaymentStatus.PAID)).thenReturn(true);

        schedulerService.completePaidOrders();

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(OrderStatus.DELIVERED, saved.getStatus());

        verify(paymentService)
                .updatePaymentStatusByOrderId(4L, PaymentStatus.PAID);
    }
}
