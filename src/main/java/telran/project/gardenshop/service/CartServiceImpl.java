package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.CartNotFoundException;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.mapper.CartMapper;
import telran.project.gardenshop.repository.CartRepository;
import telran.project.gardenshop.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserService userService;

    @Override
    public CartResponseDto getByUser(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart for user with id " + user.getId() + " not found"));
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto getOrCreateForCurrentUser() {
        User currentUser = userService.getCurrentUser()
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(currentUser)
                            .build();
                    return cartRepository.save(newCart);
                });
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponseDto update(Cart cart) {
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDto(updatedCart);
    }

    @Override
    public CartResponseDto addItem(Long productId) {
        User currentUser = userService.getCurrentUser()
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(currentUser)
                            .build();
                    return cartRepository.save(newCart);
                });

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            CartItem newItem = CartItem.builder()
                    .product(userService.getProductById(productId)) // Assuming userService has a method to get product
                    .quantity(1)
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartResponseDto updateItem(Long cartItemId, Integer quantity) {
        User currentUser = userService.getCurrentUser()
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new CartNotFoundException("Cart for current user not found"));

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartNotFoundException("Cart item with id " + cartItemId + " not found"));

        item.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Override
    public CartResponseDto deleteItem(Long cartItemId) {
        User currentUser = userService.getCurrentUser()
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new CartNotFoundException("Cart for current user not found"));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new CartNotFoundException("Cart item with id " + cartItemId + " not found");
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }
}
