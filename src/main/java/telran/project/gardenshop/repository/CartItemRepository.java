package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByCartAndProduct(Cart cart, Product product);

    void deleteAllByCart(Cart cart);
}