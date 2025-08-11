package telran.project.gardenshop.service;

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
import telran.project.gardenshop.exception.EmptyCartException;
import telran.project.gardenshop.exception.ProductNotInCartException;
import telran.project.gardenshop.exception.InsufficientQuantityException;
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

    @Test
    void createOrder_ShouldCreateOrderWithItems() {
        // Create order items
        OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                .productId(10L)
                .quantity(2)
                .build();
        OrderItemRequestDto item2 = OrderItemRequestDto.builder()
                .productId(20L)
                .quantity(1)
                .build();

        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setDeliveryAddress("Test Address");
        dto.setItems(List.of(item1, item2));

        User user = new User();
        user.setId(userId);
        user.setFullName("Test User");

        Product product1 = new Product();
        product1.setId(10L);
        product1.setPrice(BigDecimal.valueOf(100));

        Product product2 = new Product();
        product2.setId(20L);
        product2.setPrice(BigDecimal.valueOf(50));

        // Build cart and items with proper relationships
        Cart cart = new Cart();
        cart.setId(5L);

        CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product1)
                .quantity(3)  // >= requested quantity (2)
                .price(100d)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .cart(cart)
                .product(product2)
                .quantity(2)  // >= requested quantity (1)
                .price(50d)
                .build();

        cart.setItems(new ArrayList<>(List.of(cartItem1, cartItem2)));

        Order savedOrder = Order.builder()
                .id(123L)
                .user(user)
                .status(OrderStatus.NEW)
                .deliveryMethod("COURIER")
                .deliveryAddress("Test Address")
                .contactName("Test User")
                .createdAt(now)
                .items(new ArrayList<>())
                .build();

        when(userService.getCurrent()).thenReturn(user);
        when(cartService.get()).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.create(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.NEW, result.getStatus());
        assertEquals("Test Address", result.getDeliveryAddress());
        assertEquals("Test User", result.getContactName());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {
        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setDeliveryAddress("Some address");
        dto.setItems(List.of());

        User user = new User();
        user.setId(userId);

        Cart emptyCart = new Cart();
        emptyCart.setId(99L);
        emptyCart.setItems(new ArrayList<>());

        when(userService.getCurrent()).thenReturn(user);
        when(cartService.get()).thenReturn(emptyCart);

        assertThrows(EmptyCartException.class, () -> orderService.create(dto));
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotInCart() {
        OrderItemRequestDto item = OrderItemRequestDto.builder()
                .productId(999L)
                .quantity(1)
                .build();

        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setDeliveryAddress("Some address");
        dto.setItems(List.of(item));

        User user = new User();
        user.setId(userId);

        Cart cart = new Cart();
        cart.setId(99L);
        cart.setItems(new ArrayList<>());

        when(userService.getCurrent()).thenReturn(user);
        when(cartService.get()).thenReturn(cart);

        assertThrows(EmptyCartException.class, () -> orderService.create(dto));
    }

    @Test
    void createOrder_ShouldThrowException_WhenInsufficientQuantity() {
        OrderItemRequestDto item = OrderItemRequestDto.builder()
                .productId(10L)
                .quantity(5)
                .build();

        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setDeliveryAddress("Some address");
        dto.setItems(List.of(item));

        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(10L);
        product.setPrice(BigDecimal.valueOf(100));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .price(100d)
                .build();

        Cart cart = new Cart();
        cart.setId(99L);
        cart.setItems(List.of(cartItem));

        when(userService.getCurrent()).thenReturn(user);
        when(cartService.get()).thenReturn(cart);

        assertThrows(InsufficientQuantityException.class, () -> orderService.create(dto));
    }
}
