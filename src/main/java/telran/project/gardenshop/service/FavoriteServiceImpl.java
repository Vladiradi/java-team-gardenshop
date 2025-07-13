package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.FavoriteAlreadyExistsException;
import telran.project.gardenshop.exception.FavoriteNotFoundException;
import telran.project.gardenshop.exception.ProductNotFoundException;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.repository.FavoriteRepository;
import telran.project.gardenshop.repository.ProductRepository;
import telran.project.gardenshop.repository.UserRepository;

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

        if(favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw new FavoriteAlreadyExistsException(user.getId(), product.getId());
        }

        favorite.setUser(user);
        favorite.setProduct(product);

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFromFavorites(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException(id));
        favoriteRepository.delete(favorite);
    }

    @Override
    public List<Favorite> getAllByUserId(Long userId) {
        return favoriteRepository.findAllByUserId(userId);
    }
}