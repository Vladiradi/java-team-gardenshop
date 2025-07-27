package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.repository.CartRepository;
import telran.project.gardenshop.repository.UserRepository;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void testAddToCart_whenCartExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Cart cart = Cart.builder().id(1L).user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        CartResponseDto response = cartService.addToCart(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void testAddToCart_whenCartDoesNotExist() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Cart cart = Cart.builder().id(1L).user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponseDto response = cartService.addToCart(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testGetCartById() {
        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartById(cartId);

        assertNotNull(result);
        assertEquals(cartId, result.getId());
    }
}
