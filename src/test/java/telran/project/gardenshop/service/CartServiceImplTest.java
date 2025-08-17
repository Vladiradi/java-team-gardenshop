package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.repository.CartRepository;
import telran.project.gardenshop.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Cart testCart;
    private CartResponseDto testCartResponseDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).build();
        testCart = Cart.builder().id(1L).user(testUser).build();
        testCartResponseDto = new CartResponseDto();
        testCartResponseDto.setId(1L);
        testCartResponseDto.setUserId(1L);
    }

    @Test
    void shouldReturnExistingCart_whenCartExists() {
        when(userService.getCurrent()).thenReturn(testUser);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        Cart result = cartService.get();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void shouldCreateNewCart_whenCartDoesNotExist() {
        when(userService.getCurrent()).thenReturn(testUser);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.get();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void shouldUpdateCart_whenCartProvided() {
        when(cartRepository.save(testCart)).thenReturn(testCart);

        Cart result = cartService.update(testCart);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository).save(testCart);
    }

    @Test
    void shouldAddItemToCart_whenProductIdProvided() {
        Product testProduct = Product.builder().id(1L).build();
        when(userService.getCurrent()).thenReturn(testUser);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productService.getById(1L)).thenReturn(testProduct);
        when(productService.getCurrentPrice(testProduct)).thenReturn(BigDecimal.valueOf(10.0));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.addItem(1L);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }
}
