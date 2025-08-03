package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    Payment createPayment(Long orderId, PaymentMethod method);

    Payment getPaymentById(Long id);

    List<Payment> getAllPayments();

    Payment updatePaymentStatus(Long id, PaymentStatus status);

    void deletePayment(Long id);
}