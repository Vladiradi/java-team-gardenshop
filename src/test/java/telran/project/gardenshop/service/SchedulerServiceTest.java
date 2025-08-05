package telran.project.gardenshop.service.scheduler;

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

        Order oldOrder = new Order();
        oldOrder.setId(1L);
        oldOrder.setCreatedAt(LocalDateTime.now().minusDays(1));
        oldOrder.setStatus(OrderStatus.NEW);

        Order recentOrder = new Order();
        recentOrder.setId(2L);
        recentOrder.setCreatedAt(LocalDateTime.now());
        recentOrder.setStatus(OrderStatus.NEW);

        when(orderRepository.findAllByStatus(OrderStatus.NEW))
                .thenReturn(List.of(oldOrder, recentOrder));
        when(paymentService.isPaymentStatus(1L, PaymentStatus.UNPAID)).thenReturn (true);


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
    void testCompletePaidOrders_SkipsWhenPaymentNotPaid() {

        Order order = new Order();
        order.setId(3L);
        order.setCreatedAt(LocalDateTime.now().minusDays(1));
        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findAllByStatus(OrderStatus.PAID))
                .thenReturn(List.of(order));
        when(paymentService.isPaymentStatus(3L, PaymentStatus.PAID)).thenReturn(false);

        schedulerService.completePaidOrders();

        verify(orderRepository, never()).save(any());
        verify(paymentService, never()).updatePaymentStatusByOrderId(anyLong(), any());
    }

    @Test
    void testCompletePaidOrders_ProcessesPaidOrders() {

        Order order = new Order();
        order.setId(4L);
        order.setCreatedAt(LocalDateTime.now().minusDays(1));
        order.setStatus(OrderStatus.PAID);

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
