package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest extends AbstractTest {

        private MockMvc mockMvc;

        @Mock
        private PaymentService paymentService;

        @InjectMocks
        private PaymentController paymentController;

        private ObjectMapper objectMapper;

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("POST /v1/payments - Create payment successfully")
        void createPayment_returnsCreatedPayment() throws Exception {
                Payment payment = Payment.builder()
                                .id(1L)
                                .status(PaymentStatus.UNPAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(LocalDateTime.now())
                                .build();

                when(paymentService.createPayment(eq(1L), eq(PaymentMethod.CARD))).thenReturn(payment);

                mockMvc.perform(post("/v1/payments")
                                .param("orderId", "1")
                                .param("method", "CARD"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.id").value(payment.getId()),
                                                jsonPath("$.status").value(payment.getStatus().name()),
                                                jsonPath("$.method").value(payment.getMethod().name()));
        }

        @Test
        @DisplayName("GET /v1/payments/{id} - Get payment by ID")
        void getPaymentById_returnsPayment() throws Exception {
                Payment payment = Payment.builder()
                                .id(1L)
                                .status(PaymentStatus.UNPAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(LocalDateTime.now())
                                .build();

                when(paymentService.getPaymentById(1L)).thenReturn(payment);

                mockMvc.perform(get("/v1/payments/{id}", 1L))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.id").value(payment.getId()),
                                                jsonPath("$.status").value(payment.getStatus().name()),
                                                jsonPath("$.method").value(payment.getMethod().name()));
        }

        @Test
        @DisplayName("GET /v1/payments - Get all payments")
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

                mockMvc.perform(get("/v1/payments"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.length()").value(2),
                                                jsonPath("$[0].id").value(payment1.getId()),
                                                jsonPath("$[1].id").value(payment2.getId()));
        }

        @Test
        @DisplayName("PUT /v1/payments/{id}/status - Update payment status")
        void updatePaymentStatus_returnsUpdatedPayment() throws Exception {
                Payment updatedPayment = Payment.builder()
                                .id(1L)
                                .status(PaymentStatus.PAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(LocalDateTime.now())
                                .build();

                when(paymentService.updatePaymentStatus(1L, PaymentStatus.PAID)).thenReturn(updatedPayment);

                mockMvc.perform(put("/v1/payments/{id}/status", 1L)
                                .param("status", "PAID"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.id").value(updatedPayment.getId()),
                                                jsonPath("$.status").value(updatedPayment.getStatus().name()));
        }

        @Test
        @DisplayName("DELETE /v1/payments/{id} - Delete payment")
        void deletePayment_returnsNoContent() throws Exception {
                doNothing().when(paymentService).deletePayment(1L);

                mockMvc.perform(delete("/v1/payments/{id}", 1L))
                                .andDo(print())
                                .andExpect(status().isOk());

                verify(paymentService, times(1)).deletePayment(1L);
        }
}
