import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
public class SchedulerServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    SchedulerServiceImpl schedulerService;

    @Test
    void testUpdateOrderStatuses_cancelExpiredNewOrders() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now().minusMinutes(20))
                .build();

        Payment payment = Payment.builder()
                .id(10L)
                .order(order)
                .status(PaymentStatus.NEW)
                .build();

        when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of(order));
        when(orderRepository.findAllByStatus(OrderStatus.PAID)).thenReturn(List.of());
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        schedulerService.updateOrderStatuses();

        assert order.getStatus() == OrderStatus.CANCELLED;
        assert payment.getStatus() == PaymentStatus.CANCELLED;
    }
}