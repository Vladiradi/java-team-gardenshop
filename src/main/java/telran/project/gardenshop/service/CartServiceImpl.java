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
import telran.project.gardenshop.repository.CartItemRepository;
import telran.project.gardenshop.repository.CartRepository;
import telran.project.gardenshop.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final UserService userService;


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
    public void deleteItems(List<CartItem> cartItems) {
        cartItems.forEach(item -> { cartItemRepository.deleteById(item.getId()); });
    }

    @Override
    public Cart addItem(Long productId) {
        User currentUser = userService.getCurrent();
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
                    //TODO
                    //.product(userService.getProductById(productId)) // Assuming userService has a method to get product
                    .quantity(1)
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return savedCart;
    }

    @Override
    public Cart updateItem(Long cartItemId, Integer quantity) {
        User currentUser = userService.getCurrent();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new CartNotFoundException("Cart for current user not found"));

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartNotFoundException("Cart item with id " + cartItemId + " not found"));

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    public Cart deleteItem(Long cartItemId) {
        User currentUser = userService.getCurrent();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new CartNotFoundException("Cart for current user not found"));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new CartNotFoundException("Cart item with id " + cartItemId + " not found");
        }

        return cartRepository.save(cart);
    }
}
