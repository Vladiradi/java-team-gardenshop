package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.exception.PaymentNotFoundException;
import telran.project.gardenshop.repository.PaymentRepository;
import telran.project.gardenshop.exception.PaymentAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderService orderService;

    private Payment findPaymentOrThrow(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    @Override
    public Payment createPayment(Long orderId, PaymentMethod method) {
        Order order = orderService.getOrderById(orderId);

        Payment payment = Payment.builder()
                .order(order)
                .status(PaymentStatus.UNPAID)
                .method(method)
                .build();
        try {
            return paymentRepository.save(payment);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate payment prevented for order {}", orderId, ex);
            throw new PaymentAlreadyExistsException(
                    "Payment already exists for order id: " + orderId, ex
            );
        }
    }

    @Override
    public Payment getPaymentById(Long id) {
        return findPaymentOrThrow(id);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public void deletePayment(Long id) {
        findPaymentOrThrow(id);
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = findPaymentOrThrow(id);
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
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