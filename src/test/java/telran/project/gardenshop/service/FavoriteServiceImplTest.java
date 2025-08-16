package telran.project.gardenshop.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.FavoriteAlreadyExistsException;
import telran.project.gardenshop.exception.FavoriteNotFoundException;
import telran.project.gardenshop.repository.FavoriteRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    private final User user = User.builder().id(1L).build();
    private final Product product = Product.builder().id(10L).build();
    private final Favorite favorite = Favorite.builder().id(100L).user(user).product(product).build();
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    @DisplayName("Add to favorites: positive case")
    void addToFavoritesPositiveCase() {
        when(userService.getCurrent()).thenReturn(user);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()))
                .thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

        Favorite actual = favoriteService.addToFavorites(Favorite.builder().product(product).build());

        assertNotNull(actual);
        assertEquals(favorite.getId(), actual.getId());
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Add to favorites: negative case - already exists")
    void addToFavoritesNegativeCase() {
        when(userService.getCurrent()).thenReturn(user);
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()))
                .thenReturn(Optional.of(favorite));

        FavoriteAlreadyExistsException exception = assertThrows(FavoriteAlreadyExistsException.class,
                () -> favoriteService.addToFavorites(Favorite.builder().product(product).build()));

        String expectedMessage = "Favorite for user " + user.getId() + " and product " + product.getId() + " already exists";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get favorites for current user")
    void getCurrentUserFavoritesPositiveCase() {
        when(userService.getCurrent()).thenReturn(user);
        when(favoriteRepository.findAllByUserId(user.getId())).thenReturn(List.of(favorite));

        List<Favorite> actual = favoriteService.getCurrentUserFavorites();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(favorite.getId(), actual.get(0).getId());
        verify(favoriteRepository).findAllByUserId(user.getId());
    }

    @Test
    @DisplayName("Get favorite by ID: positive case")
    void getFavoriteByIdPositiveCase() {
        when(favoriteRepository.findById(100L)).thenReturn(Optional.of(favorite));

        Favorite actual = favoriteService.getFavoriteById(100L);

        assertNotNull(actual);
        assertEquals(favorite.getId(), actual.getId());
        verify(favoriteRepository).findById(100L);
    }

    @Test
    @DisplayName("Get favorite by ID: negative case - not found")
    void getFavoriteByIdNegativeCase() {
        Long favoriteId = 9999L;

        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.empty());

        FavoriteNotFoundException exception = assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.getFavoriteById(favoriteId));

        String expectedMessage = "Favorite with id " + favoriteId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update favorite: positive case")
    void updateFavoritePositiveCase() {
        when(favoriteRepository.findById(100L)).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

        Favorite actual = favoriteService.updateFavorite(100L, Favorite.builder().product(product).build());

        assertNotNull(actual);
        assertEquals(favorite.getId(), actual.getId());
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Remove favorite by ID: positive case")
    void removeFromFavoritesPositiveCase() {
        when(favoriteRepository.findById(100L)).thenReturn(Optional.of(favorite));

        favoriteService.removeFromFavorites(100L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    @DisplayName("Remove from favorites: negative case - not found")
    void removeFromFavoritesNegativeCase() {
        Long favoriteId = 9999L;

        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.empty());

        FavoriteNotFoundException exception = assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.removeFromFavorites(favoriteId));

        String expectedMessage = "Favorite with id " + favoriteId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
    }
}
