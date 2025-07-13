package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    List<Favorite> findAllByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}