package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import telran.project.gardenshop.service.PaymentService;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.dto.PaymentResponseDto;
import telran.project.gardenshop.mapper.PaymentMapper;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment", description = "Payment management operations")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create payment", description = "Create a new payment for an order. **USER role required.**")
    public ResponseEntity<PaymentResponseDto> createPayment(
            @RequestParam Long orderId,
            @RequestParam PaymentMethod method) {
        Payment payment = paymentService.createPayment(orderId, method);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDto(payment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment information by unique identifier. **USER role required.**")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentMapper.toDto(payment));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payments", description = "Retrieve a list of all payments. **ADMIN role required.**")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponseDto> dtoList = payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update payment status", description = "Update the status of an existing payment. **ADMIN role required.**")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        Payment payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(paymentMapper.toDto(payment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete payment", description = "Remove a payment from the system. **ADMIN role required.**")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
