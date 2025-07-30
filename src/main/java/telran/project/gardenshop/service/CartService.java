package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;

public interface CartService {
    CartResponseDto addToCart(Long userId);

    Cart getOrCreateCartByUserId(Long userId);

    Cart getCartById(Long cartId);
}