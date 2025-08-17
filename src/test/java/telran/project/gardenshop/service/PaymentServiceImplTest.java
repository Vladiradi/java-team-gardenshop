package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.Payment;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.exception.PaymentNotFoundException;
import telran.project.gardenshop.repository.PaymentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder().id(1L).build();
        testPayment = Payment.builder()
                .id(1L)
                .order(testOrder)
                .status(PaymentStatus.UNPAID)
                .method(PaymentMethod.CARD)
                .build();
    }

    @Test
    void shouldCreatePayment_whenOrderExists() {
        when(orderService.getById(1L)).thenReturn(testOrder);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.createPayment(1L, PaymentMethod.CARD);

        assertNotNull(payment);
        assertEquals(testOrder, payment.getOrder());
        assertEquals(PaymentStatus.UNPAID, payment.getStatus());
        assertEquals(PaymentMethod.CARD, payment.getMethod());

        verify(orderService).getById(1L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldThrowException_whenOrderNotFound() {
        when(orderService.getById(99L))
                .thenThrow(new OrderNotFoundException("Order not found with id: 99"));

        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class,
                () -> paymentService.createPayment(99L, PaymentMethod.CASH));

        assertEquals("Order not found with id: 99", ex.getMessage());
        verify(orderService).getById(99L);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void shouldReturnPayment_whenPaymentIdExists() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        Payment result = paymentService.getPaymentById(1L);

        assertEquals(testPayment, result);
        verify(paymentRepository).findById(1L);
    }

    @Test
    void shouldThrowException_whenPaymentIdNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentNotFoundException ex = assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(1L));

        assertEquals("Payment not found with id: 1", ex.getMessage());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void shouldDeletePayment_whenPaymentExists() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        doNothing().when(paymentRepository).deleteById(1L);

        paymentService.deletePayment(1L);

        verify(paymentRepository).findById(1L);
        verify(paymentRepository).deleteById(1L);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistentPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentNotFoundException ex = assertThrows(PaymentNotFoundException.class,
                () -> paymentService.deletePayment(1L));

        assertEquals("Payment not found with id: 1", ex.getMessage());
        verify(paymentRepository).findById(1L);
        verify(paymentRepository, never()).deleteById(1L);
    }

    @Test
    void shouldUpdatePaymentStatus_whenPaymentExists() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment updated = paymentService.updatePaymentStatus(1L, PaymentStatus.PAID);

        assertEquals(PaymentStatus.PAID, updated.getStatus());
        verify(paymentRepository).findById(1L);
        verify(paymentRepository).save(testPayment);
    }

    @Test
    void shouldReturnTrue_whenPaymentExistsAndStatusMatches() {
        Payment paidPayment = Payment.builder().status(PaymentStatus.PAID).build();
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(paidPayment));

        assertTrue(paymentService.isPaymentStatus(1L, PaymentStatus.PAID));
    }

    @Test
    void shouldReturnFalse_whenPaymentNotFound() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        assertFalse(paymentService.isPaymentStatus(1L, PaymentStatus.PAID));
    }

    @Test
    void shouldUpdatePaymentStatusByOrderId_whenPaymentExists() {
        Payment payment = Payment.builder()
                .order(testOrder)
                .status(PaymentStatus.UNPAID)
                .build();

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Payment> updatedOpt = paymentService.updatePaymentStatusByOrderId(1L, PaymentStatus.PAID);

        assertTrue(updatedOpt.isPresent());
        Payment updated = updatedOpt.get();
        assertEquals(PaymentStatus.PAID, updated.getStatus());
        assertNotNull(updated.getUpdatedAt());
        verify(paymentRepository).findByOrderId(1L);
        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldReturnEmpty_whenPaymentNotFoundByOrderId() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        Optional<Payment> updatedOpt = paymentService.updatePaymentStatusByOrderId(1L, PaymentStatus.PAID);
        assertTrue(updatedOpt.isEmpty());
        verify(paymentRepository).findByOrderId(1L);
    }
}
