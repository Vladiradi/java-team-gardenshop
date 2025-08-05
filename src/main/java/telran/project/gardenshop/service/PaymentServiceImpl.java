package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.exception.PaymentNotFoundException;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.PaymentRepository;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public Payment createPayment(Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        return paymentRepository.save(Payment.builder()
                .order(order)
                .status(PaymentStatus.UNPAID)
                .method(method)
                .build());
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = getPaymentById(id);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public boolean isPaymentStatus(Long orderId, PaymentStatus status) {
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> payment.getStatus() == status)
                .orElse(false);
    }

    @Override
    public Optional<Payment> updatePaymentStatusByOrderId(Long orderId, PaymentStatus status) {
        return paymentRepository.findByOrderId(orderId)
            .map(payment -> {
                payment.setStatus(status);
                payment.setUpdatedAt(LocalDateTime.now());
                return paymentRepository.save(payment);
            });
    }
}