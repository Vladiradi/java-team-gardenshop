package telran.project.gardenshop.service;

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
import telran.project.gardenshop.exception.CartNotFoundException;
import telran.project.gardenshop.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartServiceImpl cartService;

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
                .items(new ArrayList<>())
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
    void get_ExistingCart_ReturnsCart() {
        // Given
        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // When
        Cart result = cartService.get();

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        assertEquals(user, result.getUser());
        verify(cartRepository).findByUser(user);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void get_NoExistingCart_CreatesAndReturnsNewCart() {
        // Given
        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(1L);
            return savedCart;
        });

        // When
        Cart result = cartService.get();

        // Then
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void update_ValidCart_ReturnsUpdatedCart() {
        // Given
        Cart updatedCart = Cart.builder()
                .id(1L)
                .user(user)
                .items(List.of(cartItem))
                .build();

        when(cartRepository.save(updatedCart)).thenReturn(updatedCart);

        // When
        Cart result = cartService.update(updatedCart);

        // Then
        assertNotNull(result);
        assertEquals(updatedCart.getId(), result.getId());
        assertEquals(1, result.getItems().size());
        verify(cartRepository).save(updatedCart);
    }

    @Test
    void addItem_NewProduct_AddsNewCartItem() {
        // Given
        Long productId = 2L;
        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // When
        Cart result = cartService.addItem(productId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem addedItem = result.getItems().get(0);
        assertEquals(1, addedItem.getQuantity());
        assertEquals(cart, addedItem.getCart());
        verify(cartRepository).save(cart);
    }

    @Test
    void addItem_ExistingProduct_IncrementsQuantity() {
        // Given
        Long productId = 2L;
        cart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // When
        Cart result = cartService.addItem(productId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem updatedItem = result.getItems().get(0);
        assertEquals(2, updatedItem.getQuantity()); // 1 + 1
        verify(cartRepository).save(cart);
    }

    @Test
    void updateItem_ValidCartItem_UpdatesQuantity() {
        // Given
        Long cartItemId = 3L;
        Integer newQuantity = 5;
        cart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // When
        Cart result = cartService.updateItem(cartItemId, newQuantity);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem updatedItem = result.getItems().get(0);
        assertEquals(newQuantity, updatedItem.getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    void updateItem_CartItemNotFound_ThrowsCartNotFoundException() {
        // Given
        Long cartItemId = 999L;
        Integer newQuantity = 5;
        cart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // When & Then
        CartNotFoundException exception = assertThrows(CartNotFoundException.class,
                () -> cartService.updateItem(cartItemId, newQuantity));

        assertEquals("Cart item with id " + cartItemId + " not found", exception.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void deleteItem_ValidCartItem_RemovesItem() {
        // Given
        Long cartItemId = 3L;
        cart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // When
        Cart result = cartService.deleteItem(cartItemId);

        // Then
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteItem_CartItemNotFound_ThrowsCartNotFoundException() {
        // Given
        Long cartItemId = 999L;
        cart.getItems().add(cartItem);

        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // When & Then
        CartNotFoundException exception = assertThrows(CartNotFoundException.class,
                () -> cartService.deleteItem(cartItemId));

        assertEquals("Cart item with id " + cartItemId + " not found", exception.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItem_EmptyCart_CreatesNewCartItem() {
        // Given
        Long productId = 2L;
        Cart emptyCart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        when(userService.getCurrent()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(emptyCart));
        when(cartRepository.save(emptyCart)).thenReturn(emptyCart);

        // When
        Cart result = cartService.addItem(productId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem newItem = result.getItems().get(0);
        assertEquals(1, newItem.getQuantity());
        assertEquals(emptyCart, newItem.getCart());
        verify(cartRepository).save(emptyCart);
    }
}
