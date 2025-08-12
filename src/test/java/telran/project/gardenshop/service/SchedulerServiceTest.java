package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.Order;
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
public class SchedulerServiceTest {

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private PaymentService paymentService;

        @InjectMocks
        private SchedulerService schedulerService;

        @Test
        void testCancelOrders_ProcessesOnlyOldUnpaidOrders() {
                // Given
                Order oldOrder = Order.builder()
                                .id(1L)
                                .createdAt(LocalDateTime.now().minusDays(1))
                                .status(OrderStatus.NEW)
                                .build();

                Order recentOrder = Order.builder()
                                .id(2L)
                                .createdAt(LocalDateTime.now())
                                .status(OrderStatus.NEW)
                                .build();

                when(orderRepository.findAllByStatusAndCreatedAtBefore(eq(OrderStatus.NEW), any(LocalDateTime.class)))
                                .thenReturn(List.of(oldOrder, recentOrder));
                when(paymentService.isPaymentStatus(1L, PaymentStatus.UNPAID)).thenReturn(true);
                when(paymentService.isPaymentStatus(2L, PaymentStatus.UNPAID)).thenReturn(false);

                // When
                schedulerService.cancelOrders();

                // Then
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
        void testCompletePaidOrders_SkipsWhenPaymentNotPaid() {
                // Given
                Order order = Order.builder()
                                .id(3L)
                                .createdAt(LocalDateTime.now().minusDays(1))
                                .status(OrderStatus.PAID)
                                .build();

                when(orderRepository.findAllByStatusAndCreatedAtBefore(eq(OrderStatus.PAID), any(LocalDateTime.class)))
                                .thenReturn(List.of(order));
                when(paymentService.isPaymentStatus(3L, PaymentStatus.PAID)).thenReturn(false);

                // When
                schedulerService.completePaidOrders();

                // Then
                verify(orderRepository, never()).save(any());
                verify(paymentService, never()).updatePaymentStatusByOrderId(anyLong(), any());
        }

        @Test
        void testCompletePaidOrders_ProcessesPaidOrders() {
                // Given
                Order order = Order.builder()
                                .id(4L)
                                .createdAt(LocalDateTime.now().minusDays(1))
                                .status(OrderStatus.PAID)
                                .build();

                when(orderRepository.findAllByStatusAndCreatedAtBefore(eq(OrderStatus.PAID), any(LocalDateTime.class)))
                                .thenReturn(List.of(order));
                when(paymentService.isPaymentStatus(4L, PaymentStatus.PAID)).thenReturn(true);

                // When
                schedulerService.completePaidOrders();

                // Then
                ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
                verify(orderRepository).save(orderCaptor.capture());
                Order saved = orderCaptor.getValue();
                assertEquals(OrderStatus.DELIVERED, saved.getStatus());

                verify(paymentService)
                                .updatePaymentStatusByOrderId(4L, PaymentStatus.PAID);
        }
}
