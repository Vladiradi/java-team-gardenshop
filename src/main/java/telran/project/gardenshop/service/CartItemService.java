package telran.project.gardenshop.service;


import telran.project.gardenshop.dto.CartItemRequestDto;
import telran.project.gardenshop.dto.CartItemResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;

import java.util.List;

public interface CartItemService {

    CartItemResponseDto addItemToCart(Long cartId, CartItemRequestDto requestDto);
    CartItemResponseDto updateItemQuantity(Long cartId, Long productId, Integer newQuantity);
    void removeItemFromCart(Long cartId, Long productId);
    List<CartItemResponseDto> getCartItems(Long cartId);
    void clearCart(Long cartId);

}