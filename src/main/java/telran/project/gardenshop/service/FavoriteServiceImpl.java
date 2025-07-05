package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.common.fetcher.EntityFetcher;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.repository.FavoriteRepository;
import telran.project.gardenshop.repository.ProductRepository;
import telran.project.gardenshop.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EntityFetcher fetcher;

    @Override
    public Favorite addToFavorites(Favorite favorite) {
        Long userId = favorite.getUser().getId();
        Long productId = favorite.getProduct().getId();

        User user = fetcher.fetchOrThrow(userRepository, userId, "User");
        Product product = fetcher.fetchOrThrow(productRepository, productId, "Product");

        favorite.setUser(user);
        favorite.setProduct(product);

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFromFavorites(Long id) {
        Favorite favorite = fetcher.fetchOrThrow(favoriteRepository, id, "Favorite");
        favoriteRepository.delete(favorite);
    }

    @Override
    public List<Favorite> getAllByUserId(Long userId) {
        return favoriteRepository.findAllByUserId(userId);
    }
}