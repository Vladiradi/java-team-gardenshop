package telran.project.gardenshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.project.gardenshop.dto.CartItemRequestDto;
import telran.project.gardenshop.dto.CartItemResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.repository.CartItemRepository;
import telran.project.gardenshop.mapper.CartItemMapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemResponseDto addItemToCart(Long cartId, CartItemRequestDto requestDto) {
        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(requestDto.getProductId());

        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + requestDto.getQuantity());
            return cartItemMapper.toDto(cartItemRepository.save(existingItem));
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(requestDto.getQuantity())
                    .price(requestDto.getPrice())
                    .build();
            return cartItemMapper.toDto(cartItemRepository.save(newItem));
        }
    }

    @Override
    public CartItemResponseDto updateItemQuantity(Long cartId, Long productId, Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        item.setQuantity(newQuantity);
        return cartItemMapper.toDto(cartItemRepository.save(item));
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);
        cartItemRepository.deleteByCartAndProduct(cart, product);
    }

    @Override
    public List<CartItemResponseDto> getCartItems(Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        return cartItemRepository.findByCart(cart).stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void clearCart(Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        cartItemRepository.deleteAllByCart(cart);
    }
}