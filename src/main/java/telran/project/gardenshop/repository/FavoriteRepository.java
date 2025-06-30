package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.User;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
    @Modifying
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
//Можно использовать оба варианта в зависимости от ситуации:
//
//findByUser(User user) — когда у тебя уже есть объект пользователя;
//
//findByUserId(Long userId) — когда у тебя только его ID.