package telran.project.gardenshop.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;


    @Async
    @Scheduled(fixedRate = 30000)
    public void cancelOrders() {
        LocalDateTime now = LocalDateTime.now();
        processOrders(
                OrderStatus.NEW,
                OrderStatus.CANCELLED,
                PaymentStatus.UNPAID,
                now.minusMinutes(15)
        );
    }

    @Async
    @Scheduled(fixedRate = 30000)
    public void completePaidOrders() {
        LocalDateTime now = LocalDateTime.now();
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
                boolean paymentMatches = paymentService.isPaymentStatus(order.getId(), requiredPaymentStatus);

                if (paymentMatches) {
                    order.setUpdatedAt(LocalDateTime.now());
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                    updatePayment(order.getId(), requiredPaymentStatus);
                    log.debug("Order {} status changed from {} to {}", order.getId(), currentStatus, newStatus);
                }
            }
        }
    }

    private void updatePayment(Long orderId, PaymentStatus status) {
        paymentService.updatePaymentStatusByOrderId(orderId, status)
                .ifPresent(payment -> payment.setStatus(status));
    }
}