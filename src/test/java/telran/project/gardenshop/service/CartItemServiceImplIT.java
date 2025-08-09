package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import telran.project.gardenshop.configuration.SecurityConfig;
import telran.project.gardenshop.dto.CartItemRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.repository.*;
import telran.project.gardenshop.service.security.JwtService;
import telran.project.gardenshop.mapper.CartItemMapper;
import org.springframework.security.test.context.support.WithMockUser;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(SecurityConfig.class)
class CartItemServiceImplIT {

    @Autowired
    private CartItemService cartItemService;

    @MockBean
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CartService cartService;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @MockBean
    private CartItemMapper cartItemMapper;

    @Test
    @Transactional
    @WithMockUser
    void addItemToCart_RollbackOnException() {
        User user = userRepository.save(User.builder().email("test@example.com").fullName("Test").password("123456789").role(Role.USER).build());
        Cart cart = cartRepository.save(Cart.builder().user(user).build());
        Category category = categoryRepository.save(Category.builder().id(1L).name("test cat").build());
        Product product = productRepository.save(Product.builder().name("Test").category(category).price(BigDecimal.TEN).build());

        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        when(cartItemMapper.toDto(any(CartItem.class))).thenThrow(new RuntimeException("Test rollback after save"));
        
        CartItemRequestDto dto = new CartItemRequestDto(product.getId(), 1, 10.0);
        
        try {
            cartItemService.addItemToCart(cart.getId(), dto);
        } catch (RuntimeException e) {
            // expected
        }

        assertEquals(0, cartItemRepository.count());
    }
} 