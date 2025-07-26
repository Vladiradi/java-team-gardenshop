package telran.project.gardenshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.PaymentRequestDto;
import telran.project.gardenshop.dto.PaymentResponseDto;
import telran.project.gardenshop.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponseDto createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        return paymentService.createPayment(paymentRequestDto);
    }

    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping
    public List<PaymentResponseDto> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PutMapping("/{id}/status")
    public PaymentResponseDto updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        return paymentService.updatePaymentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}