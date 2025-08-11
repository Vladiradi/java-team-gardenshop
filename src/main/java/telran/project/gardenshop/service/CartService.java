package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Cart;


public interface CartService {

    Cart get();

    Cart update(Cart cart);

    Cart addItem(Long productId);

    Cart updateItem(Long cartItemId, Integer quantity);

    Cart deleteItem(Long cartItemId);
}
