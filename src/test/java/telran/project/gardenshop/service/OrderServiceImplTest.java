package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.exception.EmptyCartException;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserService userService;
    @Mock private CartService cartService;
    @Mock private ProductService productService;

    @InjectMocks private OrderServiceImpl orderService;

    private User testUser;
    private Product testProduct1;
    private Product testProduct2;
    private Cart testCart;
    private Order testOrder;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        testUser = User.builder()
                .id(1L)
                .fullName("Alice Johnson")
                .email("alice.johnson@example.com")
                .phoneNumber("+1234567890")
                .password("$2a$10$0HzYFEvYU7jsKwENj2bfN.ynV.tqvVm2hq38vvG0aeM.lCWVMFTWC")
                .role(Role.USER)
                .build();

        testProduct1 = Product.builder()
                .id(1L)
                .name("All-Purpose Plant Fertilizer")
                .price(BigDecimal.valueOf(11.99))
                .discountPrice(BigDecimal.valueOf(8.99))
                .description("Balanced NPK formula for all types of plants")
                .imageUrl("https://example.com/images/fertilizer_all_purpose.jpg")
                .build();

        testProduct2 = Product.builder()
                .id(2L)
                .name("Organic Tomato Feed")
                .price(BigDecimal.valueOf(13.99))
                .discountPrice(BigDecimal.valueOf(9.49))
                .description("Organic liquid fertilizer ideal for tomatoes and vegetables")
                .imageUrl("https://example.com/images/fertilizer_tomato_feed.jpg")
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>())
                .build();

        testOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .status(OrderStatus.NEW)
                .deliveryMethod("COURIER")
                .deliveryAddress("123 Garden Street")
                .contactName("Alice Johnson")
                .createdAt(testDateTime)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void shouldCreateOrder_whenValidRequestProvided() {
        OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .items(List.of(item1))
                .deliveryMethod(DeliveryMethod.COURIER)
                .deliveryAddress("123 Garden Street")
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct1)
                .quantity(5)
                .build();
        testCart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(testUser);
        when(cartService.get()).thenReturn(testCart);
        when(productService.getCurrentPrice(testProduct1)).thenReturn(BigDecimal.valueOf(100));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).getProduct().getId());
        assertEquals(2, result.getItems().get(0).getQuantity());
        verify(cartService).update(testCart);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowEmptyCartException_whenOrderBecomesEmptyAfterFiltering() {
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct1)
                .quantity(5)
                .build();
        testCart.getItems().add(cartItem);

        OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                .productId(999L)
                .quantity(1)
                .build();

        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .items(List.of(item1))
                .deliveryMethod(DeliveryMethod.COURIER)
                .deliveryAddress("Test Address")
                .build();

        when(userService.getCurrent()).thenReturn(testUser);
        when(cartService.get()).thenReturn(testCart);

        EmptyCartException ex = assertThrows(EmptyCartException.class,
                () -> orderService.create(requestDto));

        assertEquals("Cannot create an order with no valid items. All requested products are not available in cart or have insufficient quantity.",
                ex.getMessage());
        verify(cartService).update(testCart);
    }

    @Test
    void shouldHandleRequestedQuantityGreaterThanAvailable() {
        OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                .productId(1L)
                .quantity(10)
                .build();

        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .items(List.of(item1))
                .deliveryMethod(DeliveryMethod.COURIER)
                .deliveryAddress("Test Address")
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct1)
                .quantity(5)
                .build();
        testCart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(testUser);
        when(cartService.get()).thenReturn(testCart);
        when(productService.getCurrentPrice(testProduct1)).thenReturn(BigDecimal.valueOf(100));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
        assertTrue(testCart.getItems().isEmpty());
        verify(cartService).update(testCart);
    }

    @Test
    void shouldHandleRequestedQuantityLessThanAvailable() {
        OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .items(List.of(item1))
                .deliveryMethod(DeliveryMethod.COURIER)
                .deliveryAddress("Test Address")
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct1)
                .quantity(5)
                .build();
        testCart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(testUser);
        when(cartService.get()).thenReturn(testCart);
        when(productService.getCurrentPrice(testProduct1)).thenReturn(BigDecimal.valueOf(100));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        assertEquals(1, testCart.getItems().size());
        assertEquals(3, testCart.getItems().get(0).getQuantity());
        verify(cartService).update(testCart);
    }

    @Test
    void shouldHandleProductNotInCart_mixedRequest() {
        OrderItemRequestDto existing = OrderItemRequestDto.builder()
                .productId(1L)
                .quantity(1)
                .build();
        OrderItemRequestDto missing = OrderItemRequestDto.builder()
                .productId(999L)
                .quantity(1)
                .build();

        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .items(List.of(existing, missing))
                .deliveryMethod(DeliveryMethod.COURIER)
                .deliveryAddress("Test Address")
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct1)
                .quantity(5)
                .build();
        testCart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(testUser);
        when(cartService.get()).thenReturn(testCart);
        when(productService.getCurrentPrice(testProduct1)).thenReturn(BigDecimal.valueOf(100));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).getProduct().getId());
        verify(cartService).update(testCart);
    }

    @Test
    void shouldThrowEmptyCartException_whenCartIsEmpty() {
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .items(List.of(
                        OrderItemRequestDto.builder().productId(1L).quantity(1).build()
                ))
                .deliveryMethod(DeliveryMethod.COURIER)
                .deliveryAddress("Test Address")
                .build();

        when(userService.getCurrent()).thenReturn(testUser);
        when(cartService.get()).thenReturn(testCart);

        EmptyCartException exception = assertThrows(EmptyCartException.class,
                () -> orderService.create(requestDto));

        assertEquals("Cannot create an order with an empty cart", exception.getMessage());
        verify(cartService, never()).update(any(Cart.class));
    }

    @Test
    void getById_shouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(testOrder));

        Order result = orderService.getById(1L);

        assertEquals(testOrder, result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getById(999L));
    }

    @Test
    void getForCurrentUser_shouldReturnUserOrders() {
        when(userService.getCurrent()).thenReturn(testUser);
        when(orderRepository.findAllByUserId(1L)).thenReturn(List.of(testOrder));

        List<Order> result = orderService.getForCurrentUser();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findAllByUserId(1L);
    }

    @Test
    void getActive_shouldReturnNonCancelledOrders() {
        when(orderRepository.findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED)))
                .thenReturn(List.of(testOrder));

        List<Order> result = orderService.getActive();

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED));
    }

    @Test
    void getAll_shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        List<Order> result = orderService.getAll();

        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void cancel_shouldCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void delete_shouldDeleteOrder() {
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(testOrder));

        orderService.delete(1L);

        verify(orderRepository).delete(testOrder);
    }

    @Test
    void updateOrder_shouldUpdateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.updateOrder(testOrder);

        assertEquals(testOrder, result);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void getAllByStatus_shouldReturnOrdersByStatus() {
        when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of(testOrder));

        List<Order> result = orderService.getAllByStatus(OrderStatus.NEW);

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findAllByStatus(OrderStatus.NEW);
    }

    @Test
    void getAllByCreatedAtBetweenAndStatus_shouldReturnOrdersInDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.NEW))
                .thenReturn(List.of(testOrder));

        List<Order> result = orderService.getAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.NEW);

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findAllByCreatedAtBetweenAndStatus(startDate, endDate, OrderStatus.NEW);
    }

    @Test
    void getAllByStatusAndCreatedAtBefore_shouldReturnOrdersBeforeDate() {
        LocalDateTime date = LocalDateTime.now();
        when(orderRepository.findAllByStatusAndCreatedAtBefore(OrderStatus.NEW, date))
                .thenReturn(List.of(testOrder));

        List<Order> result = orderService.getAllByStatusAndCreatedAtBefore(OrderStatus.NEW, date);

        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findAllByStatusAndCreatedAtBefore(OrderStatus.NEW, date);
    }
}