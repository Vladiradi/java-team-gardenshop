package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.dto.PaymentResponseDto;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.mapper.PaymentMapper;
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

        @Mock
        private PaymentMapper paymentMapper;

        @InjectMocks
        private PaymentController paymentController;

        private ObjectMapper objectMapper;
        private Payment testPayment;
        private PaymentResponseDto testPaymentDto;

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
                objectMapper = new ObjectMapper();

                testPayment = Payment.builder()
                                .id(1L)
                                .status(PaymentStatus.UNPAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                testPaymentDto = PaymentResponseDto.builder()
                                .id(1L)
                                .orderId(100L)
                                .status(PaymentStatus.UNPAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(testPayment.getCreatedAt())
                                .updatedAt(testPayment.getUpdatedAt())
                                .build();
        }

        @Test
        @DisplayName("POST /v1/payments - Create payment successfully")
        void createPayment_returnsCreatedPayment() throws Exception {
                when(paymentService.createPayment(eq(1L), eq(PaymentMethod.CARD))).thenReturn(testPayment);
                when(paymentMapper.toDto(testPayment)).thenReturn(testPaymentDto);

                mockMvc.perform(post("/v1/payments")
                                .param("orderId", "1")
                                .param("method", "CARD"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isCreated(),
                                                jsonPath("$.id").value(testPaymentDto.getId()),
                                                jsonPath("$.orderId").value(testPaymentDto.getOrderId()),
                                                jsonPath("$.status").value(testPaymentDto.getStatus().name()),
                                                jsonPath("$.method").value(testPaymentDto.getMethod().name()));

                verify(paymentService, times(1)).createPayment(1L, PaymentMethod.CARD);
                verify(paymentMapper, times(1)).toDto(testPayment);
        }

        @Test
        @DisplayName("GET /v1/payments/{id} - Get payment by ID")
        void getPaymentById_returnsPayment() throws Exception {
                when(paymentService.getPaymentById(1L)).thenReturn(testPayment);
                when(paymentMapper.toDto(testPayment)).thenReturn(testPaymentDto);

                mockMvc.perform(get("/v1/payments/{id}", 1L))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.id").value(testPaymentDto.getId()),
                                                jsonPath("$.orderId").value(testPaymentDto.getOrderId()),
                                                jsonPath("$.status").value(testPaymentDto.getStatus().name()),
                                                jsonPath("$.method").value(testPaymentDto.getMethod().name()));

                verify(paymentService, times(1)).getPaymentById(1L);
                verify(paymentMapper, times(1)).toDto(testPayment);
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

                PaymentResponseDto dto1 = PaymentResponseDto.builder()
                                .id(1L)
                                .orderId(100L)
                                .status(PaymentStatus.UNPAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(payment1.getCreatedAt())
                                .build();

                PaymentResponseDto dto2 = PaymentResponseDto.builder()
                                .id(2L)
                                .orderId(200L)
                                .status(PaymentStatus.PAID)
                                .method(PaymentMethod.CASH)
                                .createdAt(payment2.getCreatedAt())
                                .build();

                when(paymentService.getAllPayments()).thenReturn(List.of(payment1, payment2));
                when(paymentMapper.toDto(payment1)).thenReturn(dto1);
                when(paymentMapper.toDto(payment2)).thenReturn(dto2);

                mockMvc.perform(get("/v1/payments"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.length()").value(2),
                                                jsonPath("$[0].id").value(dto1.getId()),
                                                jsonPath("$[0].orderId").value(dto1.getOrderId()),
                                                jsonPath("$[1].id").value(dto2.getId()),
                                                jsonPath("$[1].orderId").value(dto2.getOrderId()));

                verify(paymentService, times(1)).getAllPayments();
                verify(paymentMapper, times(1)).toDto(payment1);
                verify(paymentMapper, times(1)).toDto(payment2);
        }

        @Test
        @DisplayName("PUT /v1/payments/{id}/status - Update payment status")
        void updatePaymentStatus_returnsUpdatedPayment() throws Exception {
                Payment updatedPayment = Payment.builder()
                                .id(1L)
                                .status(PaymentStatus.PAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                PaymentResponseDto updatedDto = PaymentResponseDto.builder()
                                .id(1L)
                                .orderId(100L)
                                .status(PaymentStatus.PAID)
                                .method(PaymentMethod.CARD)
                                .createdAt(updatedPayment.getCreatedAt())
                                .updatedAt(updatedPayment.getUpdatedAt())
                                .build();

                when(paymentService.updatePaymentStatus(1L, PaymentStatus.PAID)).thenReturn(updatedPayment);
                when(paymentMapper.toDto(updatedPayment)).thenReturn(updatedDto);

                mockMvc.perform(put("/v1/payments/{id}/status", 1L)
                                .param("status", "PAID"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                jsonPath("$.id").value(updatedDto.getId()),
                                                jsonPath("$.status").value(updatedDto.getStatus().name()));

                verify(paymentService, times(1)).updatePaymentStatus(1L, PaymentStatus.PAID);
                verify(paymentMapper, times(1)).toDto(updatedPayment);
        }

        @Test
        @DisplayName("DELETE /v1/payments/{id} - Delete payment")
        void deletePayment_returnsNoContent() throws Exception {
                doNothing().when(paymentService).deletePayment(1L);

                mockMvc.perform(delete("/v1/payments/{id}", 1L))
                                .andDo(print())
                                .andExpect(status().isNoContent());

                verify(paymentService, times(1)).deletePayment(1L);
        }

}
