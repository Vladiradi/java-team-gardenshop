package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.CartResponseDto;

public interface CartService {
    CartResponseDto addToCart(Long userId);
}