package telran.project.gardenshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.CartItemRequestDto;
import telran.project.gardenshop.dto.CartItemResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.CartItemMapper;
import telran.project.gardenshop.repository.CartItemRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    private Cart cart;

    private Product product;

    private CartItem cartItem;

    private CartItemRequestDto requestDto;

    private CartItemResponseDto responseDto;

    @BeforeEach
    void setUp() {
        cart = Cart.builder().id(1L).build();

        product = Product.builder()
                .id(2L)
                .name("Test Product Name")
                .build();

        cartItem = CartItem.builder()
                .id(3L)
                .cart(cart)
                .product(product)
                .quantity(1)
                .price(10.0)
                .build();

        requestDto = new CartItemRequestDto(product.getId(), 1, 10.0);

        responseDto = CartItemResponseDto.builder()
                .id(cartItem.getId())
                .cartId(cart.getId())
                .productId(product.getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .productName(product.getName())
                .build();
    }

    @Test
    void addItemToCart_NewItem_Success() {
        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.addItemToCart(cart.getId(), requestDto);

        assertNotNull(result);
        assertEquals(product.getId(), result.getProductId());

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartItemMapper).toDto(cartItem);
    }

    @Test
    void addItemToCart_ExistingItem_QuantityUpdated() {
        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.addItemToCart(cart.getId(), requestDto);

        assertNotNull(result);
        assertEquals(product.getId(), result.getProductId());
        assertEquals(2, cartItem.getQuantity()); // 1 + 1 из requestDto

        verify(cartItemRepository).save(cartItem);
        verify(cartItemMapper).toDto(cartItem);
    }

    @Test
    void updateItemQuantity_Valid_Success() {
        int newQuantity = 5;
        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.updateItemQuantity(cart.getId(), product.getId(), newQuantity);

        assertNotNull(result);
        assertEquals(newQuantity, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
        verify(cartItemMapper).toDto(cartItem);
    }

    @Test
    void updateItemQuantity_InvalidQuantity_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartItemService.updateItemQuantity(cart.getId(), product.getId(), 0));
    }

    @Test
    void updateItemQuantity_ItemNotFound_Throws() {
        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> cartItemService.updateItemQuantity(cart.getId(), product.getId(), 3));
    }

    @Test
    void removeItemFromCart_Success() {
        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(productService.getProductById(product.getId())).thenReturn(product);

        cartItemService.removeItemFromCart(cart.getId(), product.getId());

        verify(cartItemRepository).deleteByCartAndProduct(cart, product);
    }

    @Test
    void getCartItems_Success() {
        List<CartItem> items = List.of(cartItem);
        when(cartService.getCartById(cart.getId())).thenReturn(cart);
        when(cartItemRepository.findByCart(cart)).thenReturn(items);
        when(cartItemMapper.toDto(cartItem)).thenReturn(responseDto);

        List<CartItemResponseDto> result = cartItemService.getCartItems(cart.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
    }

    @Test
    void clearCart_Success() {
        when(cartService.getCartById(cart.getId())).thenReturn(cart);

        cartItemService.clearCart(cart.getId());

        verify(cartItemRepository).deleteAllByCart(cart);
    }
}
