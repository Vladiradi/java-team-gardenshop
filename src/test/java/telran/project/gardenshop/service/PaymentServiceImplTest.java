package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.PaymentRequestDto;
import telran.project.gardenshop.dto.PaymentResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.PaymentRepository;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void testCreatePayment() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setOrderId(1L);
        request.setMethod(PaymentMethod.CARD);

        Order order = Order.builder().id(1L).build();
        Payment payment = Payment.builder().id(2L).order(order).status(PaymentStatus.NEW).method(PaymentMethod.CARD).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any())).thenReturn(payment);

        PaymentResponseDto response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(PaymentMethod.CARD, response.getMethod());
    }

    @Test
    void testGetPaymentById() {
        Order order = Order.builder().id(1L).build();
        Payment payment = Payment.builder().id(10L).order(order).status(PaymentStatus.NEW).method(PaymentMethod.CARD).build();

        when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

        PaymentResponseDto response = paymentService.getPaymentById(10L);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getOrderId());
    }
}
