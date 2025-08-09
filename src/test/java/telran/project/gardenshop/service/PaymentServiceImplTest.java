//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import telran.project.gardenshop.entity.Order;
//import telran.project.gardenshop.entity.Payment;
//import telran.project.gardenshop.enums.PaymentMethod;
//import telran.project.gardenshop.enums.PaymentStatus;
//import telran.project.gardenshop.exception.OrderNotFoundException;
//import telran.project.gardenshop.exception.PaymentNotFoundException;
//import telran.project.gardenshop.repository.PaymentRepository;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PaymentServiceImplTest {
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private OrderService orderService;
//
//    @InjectMocks
//    private PaymentServiceImpl paymentService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createPayment_whenOrderExists_thenReturnSavedPayment() {
//        Long orderId = 1L;
//        Order order = Order.builder().id(orderId).build();
//
//        when(orderService.getOrderById(orderId)).thenReturn(order);
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Payment payment = paymentService.createPayment(orderId, PaymentMethod.CARD);
//
//        assertNotNull(payment);
//        assertEquals(order, payment.getOrder());
//        assertEquals(PaymentStatus.UNPAID, payment.getStatus());
//        assertEquals(PaymentMethod.CARD, payment.getMethod());
//
//        verify(orderService).getOrderById(orderId);
//        verify(paymentRepository).save(any(Payment.class));
//    }
//
//    @Test
//    void createPayment_whenOrderNotFound_thenThrowException() {
//        Long orderId = 99L;
//        when(orderService.getOrderById(orderId))
//                .thenThrow(new OrderNotFoundException("Order not found with id: " + orderId));
//
//        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () ->
//                paymentService.createPayment(orderId, PaymentMethod.CASH));
//
//        assertEquals("Order not found with id: " + orderId, ex.getMessage());
//        verify(orderService).getOrderById(orderId);
//        verifyNoMoreInteractions(paymentRepository);
//    }
//
//    @Test
//    void getPaymentById_whenExists_thenReturnPayment() {
//        Long paymentId = 1L;
//        Payment payment = Payment.builder().id(paymentId).build();
//
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
//
//        Payment result = paymentService.getPaymentById(paymentId);
//
//        assertEquals(payment, result);
//        verify(paymentRepository).findById(paymentId);
//    }
//
//    @Test
//    void getPaymentById_whenNotFound_thenThrowException() {
//        Long paymentId = 1L;
//
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
//
//        PaymentNotFoundException ex = assertThrows(PaymentNotFoundException.class, () ->
//                paymentService.getPaymentById(paymentId));
//
//        assertEquals("Payment not found with id: " + paymentId, ex.getMessage());
//        verify(paymentRepository).findById(paymentId);
//    }
//
//    @Test
//    void deletePayment_whenExists_thenDelete() {
//        Long paymentId = 1L;
//
//        Payment payment = Payment.builder().id(paymentId).build();
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
//        doNothing().when(paymentRepository).deleteById(paymentId);
//
//        paymentService.deletePayment(paymentId);
//
//        verify(paymentRepository).findById(paymentId);
//        verify(paymentRepository).deleteById(paymentId);
//    }
//
//    @Test
//    void deletePayment_whenNotFound_thenThrowException() {
//        Long paymentId = 1L;
//
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
//
//        PaymentNotFoundException ex = assertThrows(PaymentNotFoundException.class, () ->
//                paymentService.deletePayment(paymentId));
//
//        assertEquals("Payment not found with id: " + paymentId, ex.getMessage());
//        verify(paymentRepository).findById(paymentId);
//        verify(paymentRepository, never()).deleteById(paymentId);
//    }
//
//    @Test
//    void updatePaymentStatus_whenExists_thenUpdateAndSave() {
//        Long paymentId = 1L;
//        Payment payment = Payment.builder().id(paymentId).status(PaymentStatus.UNPAID).build();
//
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Payment updated = paymentService.updatePaymentStatus(paymentId, PaymentStatus.PAID);
//
//        assertEquals(PaymentStatus.PAID, updated.getStatus());
//        verify(paymentRepository).findById(paymentId);
//        verify(paymentRepository).save(payment);
//    }
//
//    @Test
//    void isPaymentStatus_whenPaymentExistsAndStatusMatches_thenReturnTrue() {
//        Long orderId = 1L;
//        Payment payment = Payment.builder().status(PaymentStatus.PAID).build();
//
//        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
//
//        assertTrue(paymentService.isPaymentStatus(orderId, PaymentStatus.PAID));
//    }
//
//    @Test
//    void isPaymentStatus_whenPaymentNotFound_thenReturnFalse() {
//        Long orderId = 1L;
//
//        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
//
//        assertFalse(paymentService.isPaymentStatus(orderId, PaymentStatus.PAID));
//    }
//
//    @Test
//    void updatePaymentStatusByOrderId_whenPaymentExists_thenUpdate() {
//        Long orderId = 1L;
//        Payment payment = Payment.builder().order(Order.builder().id(orderId).build()).status(PaymentStatus.UNPAID).build();
//
//        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Optional<Payment> updatedOpt = paymentService.updatePaymentStatusByOrderId(orderId, PaymentStatus.PAID);
//
//        assertTrue(updatedOpt.isPresent());
//        Payment updated = updatedOpt.get();
//        assertEquals(PaymentStatus.PAID, updated.getStatus());
//        assertNotNull(updated.getUpdatedAt());
//        verify(paymentRepository).findByOrderId(orderId);
//        verify(paymentRepository).save(payment);
//    }
//
//    @Test
//    void updatePaymentStatusByOrderId_whenPaymentNotFound_thenThrowException() {
//        Long orderId = 1L;
//
//        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
//
//        Optional<Payment> updatedOpt = paymentService.updatePaymentStatusByOrderId(orderId, PaymentStatus.PAID);
//        assertTrue(updatedOpt.isEmpty());
//        verify(paymentRepository).findByOrderId(orderId);
//    }
//}
