package telran.project.gardenshop.service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    Scheduler scheduler;

    @Test
    void testUpdateOrderStatuses_shouldCancelNewOrder() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now().minusMinutes(20))
                .build();

        Payment payment = Payment.builder()
                .id(101L)
                .order(order)
                .status(PaymentStatus.NEW)
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of(order));
        when(orderRepository.findAllByStatus(OrderStatus.PAID)).thenReturn(List.of());
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        scheduler.updateOrderStatuses();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());

        verify(paymentRepository).findByOrderId(1L);
    }

    @Test
    void testUpdateOrderStatuses_shouldDeliverPaidOrder() {
        Order order = Order.builder()
                .id(2L)
                .status(OrderStatus.PAID)
                .createdAt(LocalDateTime.now().minusMinutes(15))
                .build();

        Payment payment = Payment.builder()
                .id(202L)
                .order(order)
                .status(PaymentStatus.PAID)
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of());
        when(orderRepository.findAllByStatus(OrderStatus.PAID)).thenReturn(List.of(order));
        when(paymentRepository.findByOrderId(2L)).thenReturn(Optional.of(payment));

        scheduler.updateOrderStatuses();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertEquals(PaymentStatus.DELIVERED, payment.getStatus());

        verify(paymentRepository).findByOrderId(2L);
    }
}