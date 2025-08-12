package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User user;
    private Product product;
    private Favorite favorite;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).fullName("Test User").build();
        product = Product.builder().id(2L).name("Test Product").build();
        favorite = Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addToFavorites_success() {
        when(userService.getCurrent()).thenReturn(user);
        when(productService.getProductById(2L)).thenReturn(product);
        when(favoriteRepository.findByUserIdAndProductId(1L, 2L)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

        Favorite result = favoriteService.addToFavorites(2L);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(product, result.getProduct());
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_alreadyExists_throwsException() {
        when(userService.getCurrent()).thenReturn(user);
        when(productService.getProductById(2L)).thenReturn(product);
        when(favoriteRepository.findByUserIdAndProductId(1L, 2L)).thenReturn(Optional.of(favorite));

        assertThrows(FavoriteAlreadyExistsException.class,
                () -> favoriteService.addToFavorites(2L));
    }

    @Test
    void removeFromFavorites_success() {
        when(userService.getCurrent()).thenReturn(user);
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));

        favoriteService.removeFromFavorites(1L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void removeFromFavorites_notFound_throwsException() {
        when(userService.getCurrent()).thenReturn(user);
        when(favoriteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.removeFromFavorites(1L));
    }

    @Test
    void removeFromFavorites_wrongUser_throwsException() {
        User otherUser = User.builder().id(999L).fullName("Other User").build();
        when(userService.getCurrent()).thenReturn(otherUser);
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));

        assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.removeFromFavorites(1L));
    }

    @Test
    void getAll_returnsCurrentUserFavorites() {
        List<Favorite> favorites = List.of(favorite);
        when(userService.getCurrent()).thenReturn(user);
        when(favoriteRepository.findAllByUserId(1L)).thenReturn(favorites);

        List<Favorite> result = favoriteService.getAll();

        assertEquals(1, result.size());
        assertEquals(favorite, result.get(0));
    }

    @Test
    void getFavoriteById_found() {
        when(userService.getCurrent()).thenReturn(user);
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));

        Favorite result = favoriteService.getFavoriteById(1L);

        assertEquals(favorite, result);
    }

    @Test
    void getFavoriteById_notFound_throwsException() {
        when(userService.getCurrent()).thenReturn(user);
        when(favoriteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.getFavoriteById(1L));
    }

    @Test
    void getFavoriteById_wrongUser_throwsException() {
        User otherUser = User.builder().id(999L).fullName("Other User").build();
        when(userService.getCurrent()).thenReturn(otherUser);
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));

        assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.getFavoriteById(1L));
    }
}
