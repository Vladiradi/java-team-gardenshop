package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.FavoriteAlreadyExistsException;
import telran.project.gardenshop.exception.FavoriteNotFoundException;
import telran.project.gardenshop.repository.FavoriteRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserService userService;

    private final ProductService productService;

    @Override
    public Favorite addToFavorites(Long productId) {
        User user = userService.getCurrent();
        Product product = productService.getProductById(productId);

        if(favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw new FavoriteAlreadyExistsException(user.getId(), product.getId());
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFromFavorites(Long id) {
        User currentUser = userService.getCurrent();
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException(id));
        
        // Ensure the favorite belongs to the current user
        if (!favorite.getUser().getId().equals(currentUser.getId())) {
            throw new FavoriteNotFoundException(id);
        }
        
        favoriteRepository.delete(favorite);
    }

    @Override
    public List<Favorite> getAll() {
        User user = userService.getCurrent();
        return favoriteRepository.findAllByUserId(user.getId());
    }

    @Override
    public Favorite getFavoriteById(Long id) {
        User currentUser = userService.getCurrent();
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new FavoriteNotFoundException(id));
        
        // Ensure the favorite belongs to the current user
        if (!favorite.getUser().getId().equals(currentUser.getId())) {
            throw new FavoriteNotFoundException(id);
        }
        
        return favorite;
    }
}
