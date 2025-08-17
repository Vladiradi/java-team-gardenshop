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
                                .fullName("Test User")
                                .build();

                testProduct1 = Product.builder()
                                .id(10L)
                                .price(BigDecimal.valueOf(100))
                                .build();

                testProduct2 = Product.builder()
                                .id(20L)
                                .price(BigDecimal.valueOf(50))
                                .build();

                testCart = Cart.builder()
                                .id(5L)
                                .items(new ArrayList<>())
                                .build();

                testOrder = Order.builder()
                                .id(123L)
                                .user(testUser)
                                .status(OrderStatus.NEW)
                                .deliveryMethod("COURIER")
                                .deliveryAddress("Test Address")
                                .contactName("Test User")
                                .createdAt(testDateTime)
                                .items(new ArrayList<>())
                                .build();
        }

        @Test
        void shouldCreateOrder_whenValidRequestProvided() {
                OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                                .productId(10L)
                                .quantity(2)
                                .build();
                OrderItemRequestDto item2 = OrderItemRequestDto.builder()
                                .productId(20L)
                                .quantity(1)
                                .build();

                OrderCreateRequestDto dto = OrderCreateRequestDto.builder()
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Test Address")
                                .items(List.of(item1, item2))
                                .build();

                CartItem cartItem1 = CartItem.builder()
                                .id(1L)
                                .product(testProduct1)
                                .quantity(3)
                                .price(100d)
                                .build();

                CartItem cartItem2 = CartItem.builder()
                                .id(2L)
                                .product(testProduct2)
                                .quantity(2)
                                .price(50d)
                                .build();

                testCart.setItems(List.of(cartItem1, cartItem2));

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);
                when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

                Order result = orderService.create(dto);

                assertNotNull(result);
                assertEquals(OrderStatus.NEW, result.getStatus());
                assertEquals("Test Address", result.getDeliveryAddress());
                assertEquals("Test User", result.getContactName());
                verify(orderRepository).save(any(Order.class));
        }

        @Test
        void shouldThrowException_whenCartIsEmpty() {
                OrderCreateRequestDto dto = OrderCreateRequestDto.builder()
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Some address")
                                .items(List.of())
                                .build();

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);

                assertThrows(EmptyCartException.class, () -> orderService.create(dto));
        }

        @Test
        void shouldThrowException_whenProductNotInCart() {
                OrderItemRequestDto item = OrderItemRequestDto.builder()
                                .productId(999L)
                                .quantity(1)
                                .build();

                OrderCreateRequestDto dto = OrderCreateRequestDto.builder()
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Some address")
                                .items(List.of(item))
                                .build();

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);

                assertThrows(EmptyCartException.class, () -> orderService.create(dto));
        }

        @Test
        void shouldThrowException_whenInsufficientQuantity() {
                OrderItemRequestDto item = OrderItemRequestDto.builder()
                                .productId(10L)
                                .quantity(5)
                                .build();

                OrderCreateRequestDto dto = OrderCreateRequestDto.builder()
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Some address")
                                .items(List.of(item))
                                .build();

                CartItem cartItem = CartItem.builder()
                                .id(1L)
                                .product(testProduct1)
                                .quantity(2)
                                .price(100d)
                                .build();

                testCart.setItems(List.of(cartItem));

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);

                assertThrows(InsufficientQuantityException.class, () -> orderService.create(dto));
        }
}
