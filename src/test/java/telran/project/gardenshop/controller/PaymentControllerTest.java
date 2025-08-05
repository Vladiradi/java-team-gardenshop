package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.service.PaymentService;
import telran.project.gardenshop.service.security.JwtFilter;
import telran.project.gardenshop.service.security.JwtService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPayment_returnsCreatedPayment() throws Exception {
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.UNPAID)
                .method(PaymentMethod.CARD)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.createPayment(eq(1L), eq(PaymentMethod.CARD))).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .param("orderId", "1")
                        .param("method", "CARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()))
                .andExpect(jsonPath("$.status").value(payment.getStatus().name()))
                .andExpect(jsonPath("$.method").value(payment.getMethod().name()));
    }

    @Test
    void getPaymentById_returnsPayment() throws Exception {
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.UNPAID)
                .method(PaymentMethod.CARD)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        mockMvc.perform(get("/api/payments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()))
                .andExpect(jsonPath("$.status").value(payment.getStatus().name()))
                .andExpect(jsonPath("$.method").value(payment.getMethod().name()));
    }

    @Test
    void getAllPayments_returnsList() throws Exception {
        Payment payment1 = Payment.builder()
                .id(1L)
                .status(PaymentStatus.UNPAID)
                .method(PaymentMethod.CARD)
                .createdAt(LocalDateTime.now())
                .build();

        Payment payment2 = Payment.builder()
                .id(2L)
                .status(PaymentStatus.PAID)
                .method(PaymentMethod.CASH)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getAllPayments()).thenReturn(List.of(payment1, payment2));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(payment1.getId()))
                .andExpect(jsonPath("$[1].id").value(payment2.getId()));
    }

    @Test
    void updatePaymentStatus_returnsUpdatedPayment() throws Exception {
        Payment updatedPayment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.PAID)
                .method(PaymentMethod.CARD)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.updatePaymentStatus(1L, PaymentStatus.PAID)).thenReturn(updatedPayment);

        mockMvc.perform(put("/api/payments/{id}/status", 1L)
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedPayment.getId()))
                .andExpect(jsonPath("$.status").value(updatedPayment.getStatus().name()));
    }

    @Test
    void deletePayment_returnsNoContent() throws Exception {
        doNothing().when(paymentService).deletePayment(1L);

        mockMvc.perform(delete("/api/payments/{id}", 1L))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).deletePayment(1L);
    }
}
