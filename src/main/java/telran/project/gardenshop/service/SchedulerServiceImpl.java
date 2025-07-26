package telran.project.gardenshop.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl {

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    @Scheduled(fixedRate = 30000)
    public void updateOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running scheduler at {}", now);

        // new -> cancelled (15 min)
        List<Order> newOrders = orderRepository.findAllByStatus(OrderStatus.NEW);
        for (Order order : newOrders) {
            if (order.getCreatedAt().isBefore(now.minusMinutes(15))) {
                order.setStatus(OrderStatus.CANCELLED);
                updatePayment(order.getId(), PaymentStatus.CANCELLED);
                log.info("Order {} cancelled by scheduler", order.getId());
            }
        }

        // paid -> delivered (10 min)
        List<Order> paidOrders = orderRepository.findAllByStatus(OrderStatus.PAID);
        for (Order order : paidOrders) {
            if (order.getCreatedAt().isBefore(now.minusMinutes(10))) {
                order.setStatus(OrderStatus.DELIVERED);
                updatePayment(order.getId(), PaymentStatus.DELIVERED);
                log.info("Order {} delivered by scheduler", order.getId());
            }
        }
    }

    private void updatePayment(Long orderId, PaymentStatus status) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            payment.setStatus(status);
        });
    }
}
