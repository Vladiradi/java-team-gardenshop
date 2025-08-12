package telran.project.gardenshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.repository.CartItemRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        product = Product.builder()
                .id(2L)
                .name("Test Product")
                .build();

        cartItem = CartItem.builder()
                .id(3L)
                .cart(cart)
                .product(product)
                .quantity(1)
                .price(10.0)
                .build();
    }

    @Test
    void getById_CartItemNotFound_ThrowsEntityNotFoundException() {
        // Given
        Long cartItemId = 999L;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.getById(cartItemId));

        assertEquals("CartItem with id " + cartItemId + " not found", exception.getMessage());
    }

    @Test
    void getById_CartItemHasNoCart_ThrowsEntityNotFoundException() {
        // Given
        Long cartItemId = 3L;
        CartItem cartItemWithoutCart = CartItem.builder()
                .id(cartItemId)
                .cart(null)
                .product(product)
                .quantity(1)
                .price(10.0)
                .build();

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItemWithoutCart));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.getById(cartItemId));

        assertEquals("CartItem with id " + cartItemId + " not found", exception.getMessage());
    }

    @Test
    void getById_CartHasNoUser_ThrowsEntityNotFoundException() {
        // Given
        Long cartItemId = 3L;
        Cart cartWithoutUser = Cart.builder().id(1L).user(null).build();
        CartItem cartItemWithoutUser = CartItem.builder()
                .id(cartItemId)
                .cart(cartWithoutUser)
                .product(product)
                .quantity(1)
                .price(10.0)
                .build();

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItemWithoutUser));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.getById(cartItemId));

        assertEquals("CartItem with id " + cartItemId + " not found", exception.getMessage());
    }
}
