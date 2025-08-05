package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.service.scheduler.OrderStatusScheduler;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusSchedulerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderStatusScheduler scheduler;

    @Test
    void cancelOrders_shouldCancelOldUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now();

        Order order1 = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .createdAt(now.minusMinutes(16)) // старше 15 мин — должен быть отменён
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .status(OrderStatus.NEW)
                .createdAt(now.minusMinutes(10)) // моложе 15 мин — не должен обработаться
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of(order1, order2));
        when(paymentService.isPaymentStatus(1L, PaymentStatus.UNPAID)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        scheduler.cancelOrders();

        verify(orderRepository).save(argThat(order ->
                order.getId().equals(1L) && order.getStatus() == OrderStatus.CANCELLED
        ));

        verify(orderRepository, never()).save(argThat(order -> order.getId().equals(2L)));

        verify(paymentService).updatePaymentStatusByOrderId(1L, PaymentStatus.UNPAID);
        verify(paymentService, never()).updatePaymentStatusByOrderId(2L, PaymentStatus.UNPAID);
    }

    @Test
    void completePaidOrders_shouldDeliverOldPaidOrders() {
        LocalDateTime now = LocalDateTime.now();

        Order order1 = Order.builder()
                .id(1L)
                .status(OrderStatus.PAID)
                .createdAt(now.minusMinutes(15))  // старше 10 мин
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .status(OrderStatus.PAID)
                .createdAt(now.minusMinutes(5)) // меньше 10 мин
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.PAID)).thenReturn(List.of(order1, order2));
        when(paymentService.isPaymentStatus(1L, PaymentStatus.PAID)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        scheduler.completePaidOrders();

        verify(orderRepository).save(argThat(order ->
                order.getId().equals(1L) && order.getStatus() == OrderStatus.DELIVERED
        ));

        verify(orderRepository, never()).save(argThat(order -> order.getId().equals(2L)));

        verify(paymentService).updatePaymentStatusByOrderId(1L, PaymentStatus.PAID);
        verify(paymentService, never()).updatePaymentStatusByOrderId(2L, PaymentStatus.PAID);
    }

    @Test
    void processOrders_shouldSkipOrdersWithWrongPaymentStatus() {
        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .createdAt(now.minusMinutes(20))
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of(order));
        when(paymentService.isPaymentStatus(1L, PaymentStatus.UNPAID)).thenReturn(false);

        scheduler.cancelOrders();

        verify(orderRepository, never()).save(any());
        verify(paymentService, never()).updatePaymentStatusByOrderId(anyLong(), any());
    }
}