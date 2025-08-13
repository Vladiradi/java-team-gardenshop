package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import telran.project.gardenshop.configuration.SecurityConfig;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.repository.*;
import telran.project.gardenshop.service.security.JwtService;
import telran.project.gardenshop.mapper.OrderMapper;
import org.springframework.security.test.context.support.WithMockUser;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
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

        OrderItemRequestDto itemDto = OrderItemRequestDto.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderCreateRequestDto dto = new OrderCreateRequestDto();
        dto.setDeliveryMethod(DeliveryMethod.COURIER);
        dto.setDeliveryAddress("Test Address");
        dto.setItems(List.of(itemDto));

        // Build cart and items with proper relationships
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        CartItem cartItem = CartItem.builder()
                .id(1L)
                //.cart(cart)
                .product(product)
                .quantity(3)  // >= requested quantity (2)
                .price(10d)
                .build();

        cart.setItems(new ArrayList<>(List.of(cartItem)));

        when(userService.getCurrent()).thenReturn(user);
        when(cartService.get()).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(123L);
            return savedOrder;
        });
        // This will cause the rollback after order creation
        when(orderMapper.toDto(any(Order.class))).thenThrow(new RuntimeException("Test rollback after order creation"));

        try {
            orderService.create(dto);
        } catch (RuntimeException e) {
            // Expected exception
        }

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
