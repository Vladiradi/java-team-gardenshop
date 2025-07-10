package telran.project.gardenshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.FavoriteAlreadyExistsException;
import telran.project.gardenshop.exception.FavoriteNotFoundException;
import telran.project.gardenshop.repository.FavoriteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserService userService;

    private final ProductService productService;

    @Override
    public Favorite addToFavorites(Favorite favorite) {
        User user = userService.getUserById(favorite.getUser().getId());
        Product product = productService.getProductById(favorite.getProduct().getId());

        if (favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw new FavoriteAlreadyExistsException(user.getId(), product.getId());
        }

        favorite.setUser(user);
        favorite.setProduct(product);

        return favoriteRepository.save(favorite);
    }

    @Transactional
    @Override
    public void removeFromFavorites(Long productId, Long userId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        } else {
            throw new FavoriteNotFoundException(userId, productId);
        }
    }

    @Override
    public List<Favorite> getAllByUserId(Long userId) {
        return favoriteRepository.findAllByUserId(userId);
    }
}