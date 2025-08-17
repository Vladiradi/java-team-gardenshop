package telran.project.gardenshop.service;

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
        User user = userService.getCurrent();
        Product product = productService.getById(favorite.getProduct().getId());

        if(favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw new FavoriteAlreadyExistsException(user.getId(), product.getId());
        }

        favorite.setUser(user);
        favorite.setProduct(product);

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFromFavorites(Long id) {
        Favorite favorite = getFavoriteById(id);
        favoriteRepository.delete(favorite);
    }

    @Override
    public List<Favorite> getCurrentUserFavorites() {
        User user = userService.getCurrent();
        return favoriteRepository.findAllByUserId(user.getId());
    }

    @Override
    public Favorite getFavoriteById(Long id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException(id));
    }

    @Override
    public Favorite updateFavorite(Long id, Favorite updatedFavorite) {
        Favorite existingFavorite = getFavoriteById(id);
        User user = userService.getCurrent();
        Product product = productService.getById(updatedFavorite.getProduct().getId());
        existingFavorite.setUser(user);
        existingFavorite.setProduct(product);

        return favoriteRepository.save(existingFavorite);
    }
}
