package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.CartNotFoundException;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.mapper.CartMapper;
import telran.project.gardenshop.repository.CartItemRepository;
import telran.project.gardenshop.repository.CartRepository;
import telran.project.gardenshop.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserService userService;

    private final ProductService productService;

    @Override
    public Cart get() {
        User currentUser = userService.getCurrent();

        return cartRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(currentUser)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart update(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Cart addItem(Long productId) {
        Cart cart = get();

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            Product product = productService.getProductById(productId);

            CartItem newItem = CartItem.builder()
                    .product(product)
                    //todo impl discount logic in productService
                    .price(product.getPrice().doubleValue())
                    .quantity(1)
                    .build();
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItem(Long cartItemId, Integer quantity) {
        Cart cart = get();

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartNotFoundException("Cart item with id " + cartItemId + " not found"));

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    public Cart deleteItem(Long cartItemId) {
        Cart cart = get();

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new CartNotFoundException("Cart item with id " + cartItemId + " not found");
        }

        return cartRepository.save(cart);
    }
}
