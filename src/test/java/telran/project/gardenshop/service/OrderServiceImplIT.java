package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import telran.project.gardenshop.configuration.SecurityConfig;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.repository.*;
import telran.project.gardenshop.service.security.JwtService;
import telran.project.gardenshop.service.CartService;
import telran.project.gardenshop.service.CartItemService;
import telran.project.gardenshop.service.UserService;
import telran.project.gardenshop.service.ProductService;
import telran.project.gardenshop.mapper.OrderMapper;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(SecurityConfig.class)
class OrderServiceImplIT {
    @Autowired
    private OrderService orderService;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private OrderItemRepository orderItemRepository;
    @MockBean
    private CartRepository cartRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CartService cartService;
    @MockBean
    private CartItemService cartItemService;
    @MockBean
    private UserService userService;
    @MockBean
    private ProductService productService;
    @MockBean
    private OrderMapper orderMapper;

    @Test
    @Transactional
    @WithMockUser
    void createOrder_RollbackOnException() {
        User user = User.builder().id(1L).email("test@example.com").fullName("Test User").password("123456789").role(Role.USER).build();
        Category category = Category.builder().id(1L).name("test cat").build();
        Product product = Product.builder().id(1L).name("Test Product").category(category).price(BigDecimal.TEN).build();
        Cart cart = Cart.builder().id(1L).user(user).build();
        CartItem cartItem = CartItem.builder().id(1L).cart(cart).product(product).quantity(2).price(10.0).build();
        cart.setItems(List.of(cartItem));
        
        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setAddress("Test Address");
        dto.setContactName("Test Name");
        dto.setCreatedAt(LocalDateTime.now());
        
        // Настраиваем моки
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(cartService.getCartByUserId(user.getId())).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(123L);
            return savedOrder;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Мокаем mapper чтобы он выбрасывал исключение после создания заказа
        when(orderMapper.toDto(any(Order.class))).thenThrow(new RuntimeException("Test rollback after order creation"));
        
        try {
            orderService.createOrder(user.getId(), dto);
        } catch (RuntimeException e) {
            // expected
        }
        
        // Проверяем, что после rollback заказ не остался в БД
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(cartItemService, times(1)).clearCart(cart.getId());
    }
} 