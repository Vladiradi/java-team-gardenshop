package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.User;

public interface CartService {

    Cart getByUser(User user);

    Cart getOrCreateForCurrentUser();

    Cart update(Cart cart);

    Cart addItem(Long productId);

    Cart updateItem(Long cartItemId, Integer quantity);

    Cart deleteItem(Long cartItemId);
}
