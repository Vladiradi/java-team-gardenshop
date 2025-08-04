package telran.project.gardenshop.service;

import java.util.List;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;

public interface PaymentService {

    Payment createPayment(Long orderId, PaymentMethod method);

    Payment getPaymentById(Long id);

    List<Payment> getAllPayments();

    Payment updatePaymentStatus(Long id, PaymentStatus status);

    void deletePayment(Long id);

    boolean isPaymentStatus(Long orderId, PaymentStatus status);

    Payment updatePaymentStatusByOrderId(Long orderId, PaymentStatus status);
}