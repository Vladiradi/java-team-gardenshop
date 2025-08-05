package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.repository.OrderItemRepository;
import telran.project.gardenshop.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private final Long userId = 1L;
    private final LocalDateTime now = LocalDateTime.now();
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private CartService cartService;
    @Mock
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldCreateOrderWithItemsAndClearCart() {
        // GIVEN
        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setAddress("Test Address");
        dto.setContactName("Test Name");
        dto.setCreatedAt(now);

        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(10L);
        product.setPrice(BigDecimal.valueOf(100));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        Cart cart = new Cart();
        cart.setId(5L);
        cart.setItems(List.of(cartItem));

        Order savedOrder = Order.builder()
                .id(123L)
                .user(user)
                .status(OrderStatus.NEW)
                .deliveryMethod("COURIER")
                .deliveryAddress("Test Address")
                .contactName("Test Name")
                .createdAt(now)
                .items(new java.util.ArrayList<>())
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(cartService.getCartByUserId(userId)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        Order result = orderService.createOrder(userId, dto);

        // THEN
        assertNotNull(result);
        assertEquals(OrderStatus.NEW, result.getStatus());
        assertEquals("Test Address", result.getDeliveryAddress());
        assertEquals("Test Name", result.getContactName());
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(cartItemService).clearCart(cart.getId());
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {

        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setAddress("Some address");
        dto.setContactName("John Doe");
        dto.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(userId);

        Cart emptyCart = new Cart();
        emptyCart.setId(99L);
        emptyCart.setItems(new ArrayList<>());

        when(userService.getUserById(userId)).thenReturn(user);
        when(cartService.getCartByUserId(userId)).thenReturn(emptyCart);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(userId, dto));

        assertEquals("Cannot create an order with an empty cart", exception.getMessage());
    }
}
