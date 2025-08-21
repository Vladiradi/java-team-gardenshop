package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import telran.project.gardenshop.service.PaymentService;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;

import java.util.List;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment", description = "Payment management operations")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create payment", description = "Create a new payment for an order")
    public Payment createPayment(
            @RequestParam Long orderId,
            @RequestParam PaymentMethod method) {
        return paymentService.createPayment(orderId, method);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment information by unique identifier")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieve a list of all payments")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Update the status of an existing payment")
    public Payment updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        return paymentService.updatePaymentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Remove a payment from the system")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}
