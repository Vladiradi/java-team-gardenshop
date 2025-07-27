package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.PaymentRequestDto;
import telran.project.gardenshop.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);
    PaymentResponseDto getPaymentById(Long id);
    List<PaymentResponseDto> getAllPayments();
    PaymentResponseDto updatePaymentStatus(Long id, String status);
    void deletePayment(Long id);
}