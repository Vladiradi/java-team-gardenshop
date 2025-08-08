package telran.project.gardenshop.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Scheduled(fixedRateString = "${scheduler.fixed-rate}")
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
    @Scheduled(fixedRateString = "${scheduler.fixed-rate}")
    public void completePaidOrders() {
        LocalDateTime now = LocalDateTime.now();
        processOrders(
                OrderStatus.PAID,
                OrderStatus.DELIVERED,
                PaymentStatus.PAID,
                now.minusMinutes(10)
        );
    }

    @Transactional
    private void processOrders(OrderStatus currentStatus,
                               OrderStatus newStatus,
                               PaymentStatus requiredPaymentStatus,
                               LocalDateTime cutoffTime) {
        List<Order> orders = orderRepository.findAllByStatus(currentStatus);
        LocalDateTime now = LocalDateTime.now();

        orders.stream()
              .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isBefore(cutoffTime))
              .filter(order -> paymentService.isPaymentStatus(order.getId(), requiredPaymentStatus))
              .forEach(order -> {
                  order.setUpdatedAt(now);
                  order.setStatus(newStatus);
                  orderRepository.save(order);
                  paymentService.updatePaymentStatusByOrderId(order.getId(), requiredPaymentStatus);
                  log.debug("Order {} status changed from {} to {}", order.getId(), currentStatus, newStatus);
              });
    }
}