package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;

import java.util.List;

public interface CartService {

    Cart get();

    Cart update(Cart cart);

    void deleteItems(List<CartItem> cartItems);

    Cart addItem(Long productId);

    Cart updateItem(Long cartItemId, Integer quantity);

    Cart deleteItem(Long cartItemId);
}
