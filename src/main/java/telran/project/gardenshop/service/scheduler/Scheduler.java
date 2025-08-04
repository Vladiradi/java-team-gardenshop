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
                PaymentStatus.UNPAID,
                now.minusMinutes(15)
        );


        processOrders(
                OrderStatus.PAID,
                OrderStatus.DELIVERED,
                PaymentStatus.PAID,
                now.minusMinutes(10)
        );
    }

    private void processOrders(OrderStatus currentStatus,
                               OrderStatus newStatus,
                               PaymentStatus requiredPaymentStatus,
                               LocalDateTime cutoffTime) {
        List<Order> orders = orderRepository.findAllByStatus(currentStatus);
        for (Order order : orders) {
            if (order.getCreatedAt().isBefore(cutoffTime)) {
                boolean paymentMatches = paymentRepository.findByOrderId(order.getId())
                        .map(payment -> payment.getStatus() == requiredPaymentStatus)
                        .orElse(false);

                if (paymentMatches) {
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                    updatePayment(order.getId(), requiredPaymentStatus);
                    log.info("Order {} status changed from {} to {}", order.getId(), currentStatus, newStatus);
                }
            }
        }
    }

    private void updatePayment(Long orderId, PaymentStatus status) {
        paymentRepository.findByOrderId(orderId)
                .ifPresent(payment -> {
                    payment.setStatus(status);
                    paymentRepository.save(payment);
                });
    }
}