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

                List<OrderItemRequestDto> items = List.of(item1);

                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(items)
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
                when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

                Order result = orderService.create(requestDto);

                assertNotNull(result);
                assertEquals(1, result.getItems().size());
                verify(cartService).update(testCart);
        }

        @Test
        void shouldThrowEmptyCartException_whenOrderBecomesEmptyAfterFiltering() {
                OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                                .productId(999L) // Несуществующий продукт
                                .quantity(1)
                                .build();

                List<OrderItemRequestDto> items = List.of(item1);

                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(items)
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Test Address")
                                .build();

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);

                EmptyCartException exception = assertThrows(EmptyCartException.class,
                                () -> orderService.create(requestDto));

                assertEquals("Cannot create an order with no valid items. All requested products are not available in cart or have insufficient quantity.",
                                exception.getMessage());
        }

        @Test
        void shouldHandleRequestedQuantityGreaterThanAvailable() {
                OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                                .productId(10L)
                                .quantity(10) // Запрашиваем 10, но в корзине только 5
                                .build();

                List<OrderItemRequestDto> items = List.of(item1);

                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(items)
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Test Address")
                                .build();

                CartItem cartItem = CartItem.builder()
                                .id(1L)
                                .product(testProduct1)
                                .quantity(5) // В корзине только 5
                                .build();

                testCart.getItems().add(cartItem);

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);
                when(productService.getCurrentPrice(testProduct1)).thenReturn(BigDecimal.valueOf(100));
                when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

                Order result = orderService.create(requestDto);

                assertNotNull(result);
                assertEquals(1, result.getItems().size());
                assertEquals(5, result.getItems().get(0).getQuantity()); // Берем только доступное количество
                verify(cartService).update(testCart);
        }

        @Test
        void shouldHandleRequestedQuantityLessThanAvailable() {
                OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                                .productId(10L)
                                .quantity(2) // Запрашиваем 2, в корзине 5
                                .build();

                List<OrderItemRequestDto> items = List.of(item1);

                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(items)
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Test Address")
                                .build();

                CartItem cartItem = CartItem.builder()
                                .id(1L)
                                .product(testProduct1)
                                .quantity(5) // В корзине 5
                                .build();

                testCart.getItems().add(cartItem);

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart);
                when(productService.getCurrentPrice(testProduct1)).thenReturn(BigDecimal.valueOf(100));
                when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

                Order result = orderService.create(requestDto);

                assertNotNull(result);
                assertEquals(1, result.getItems().size());
                assertEquals(2, result.getItems().get(0).getQuantity()); // Берем запрошенное количество
                verify(cartService).update(testCart);
        }

        @Test
        void shouldHandleProductNotInCart() {
                OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                                .productId(10L)
                                .quantity(1)
                                .build();

                OrderItemRequestDto item2 = OrderItemRequestDto.builder()
                                .productId(999L) // Несуществующий продукт
                                .quantity(1)
                                .build();

                List<OrderItemRequestDto> items = List.of(item1, item2);

                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(items)
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
                when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

                Order result = orderService.create(requestDto);

                assertNotNull(result);
                assertEquals(1, result.getItems().size()); // Только один товар добавлен
                assertEquals(10L, result.getItems().get(0).getProduct().getId()); // Только существующий товар
                verify(cartService).update(testCart);
        }

        @Test
        void shouldThrowEmptyCartException_whenCartIsEmpty() {
                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(List.of())
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .deliveryAddress("Test Address")
                                .build();

                when(userService.getCurrent()).thenReturn(testUser);
                when(cartService.get()).thenReturn(testCart); // testCart уже пустой

                EmptyCartException exception = assertThrows(EmptyCartException.class,
                                () -> orderService.create(requestDto));

                assertEquals("Cannot create an order with an empty cart", exception.getMessage());
        }

        @Test
        void shouldThrowEmptyCartException_whenNoValidItemsAfterFiltering() {
                OrderItemRequestDto item1 = OrderItemRequestDto.builder()
                                .productId(999L) // Несуществующий продукт
                                .quantity(1)
                                .build();

                OrderItemRequestDto item2 = OrderItemRequestDto.builder()
                                .productId(888L) // Еще один несуществующий продукт
                                .quantity(1)
                                .build();

                List<OrderItemRequestDto> items = List.of(item1, item2);

                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .items(items)
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

                EmptyCartException exception = assertThrows(EmptyCartException.class,
                                () -> orderService.create(requestDto));

                assertEquals("Cannot create an order with no valid items. All requested products are not available in cart or have insufficient quantity.",
                                exception.getMessage());
        }
}
