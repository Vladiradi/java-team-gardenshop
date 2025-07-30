package telran.project.gardenshop.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 30000)
    public void updateOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();

        processOrders(
                OrderStatus.NEW,
                OrderStatus.CANCELLED,
                PaymentStatus.CANCELLED,
                now.minusMinutes(15)
        );

        processOrders(
                OrderStatus.PAID,
                OrderStatus.DELIVERED,
                PaymentStatus.DELIVERED,
                now.minusMinutes(10)
        );
    }

    private void processOrders(OrderStatus currentStatus,
                               OrderStatus newStatus,
                               PaymentStatus newPaymentStatus,
                               LocalDateTime cutoffTime) {
        List<Order> orders = orderRepository.findAllByStatus(currentStatus);
        for (Order order : orders) {
            if (order.getCreatedAt().isBefore(cutoffTime)) {
                order.setStatus(newStatus);
                updatePayment(order.getId(), newPaymentStatus);
                log.info("Order {} status changed from {} to {}", order.getId(), currentStatus, newStatus);
            }
        }
    }

    private void updatePayment(Long orderId, PaymentStatus status) {
        paymentRepository.findByOrderId(orderId)
                .ifPresent(payment -> payment.setStatus(status));
    }
}