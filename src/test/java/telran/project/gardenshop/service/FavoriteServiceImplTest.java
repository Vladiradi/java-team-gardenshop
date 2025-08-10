//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import telran.project.gardenshop.entity.Favorite;
//import telran.project.gardenshop.entity.Product;
//import telran.project.gardenshop.entity.User;
//import telran.project.gardenshop.exception.FavoriteAlreadyExistsException;
//import telran.project.gardenshop.exception.FavoriteNotFoundException;
//import telran.project.gardenshop.repository.FavoriteRepository;
//import java.util.List;
//import java.util.Optional;
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class FavoriteServiceImplTest {
//
//    @Mock
//    private FavoriteRepository favoriteRepository;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private FavoriteServiceImpl favoriteService;
//
//    private User user;
//    private Product product;
//    private Favorite favorite;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder().id(1L).fullName("Test User").build();
//        product = Product.builder().id(2L).name("Test Product").build();
//        favorite = Favorite.builder().user(user).product(product).build();
//    }
//
//    @Test
//    void addToFavorites_success() {
//        when(userService.getUserById(1L)).thenReturn(user);
//        when(productService.getProductById(2L)).thenReturn(product);
//        when(favoriteRepository.findByUserIdAndProductId(1L, 2L)).thenReturn(Optional.empty());
//        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
//
//        Favorite result = favoriteService.addToFavorites(favorite);
//
//        assertNotNull(result);
//        assertEquals(user, result.getUser());
//        assertEquals(product, result.getProduct());
//        verify(favoriteRepository).save(any(Favorite.class));
//    }
//
//    @Test
//    void addToFavorites_alreadyExists_throwsException() {
//        when(userService.getUserById(1L)).thenReturn(user);
//        when(productService.getProductById(2L)).thenReturn(product);
//        when(favoriteRepository.findByUserIdAndProductId(1L, 2L)).thenReturn(Optional.of(favorite));
//
//        assertThrows(FavoriteAlreadyExistsException.class,
//                () -> favoriteService.addToFavorites(favorite));
//    }
//
//    @Test
//    void removeFromFavorites_success() {
//        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
//
//        favoriteService.removeFromFavorites(1L);
//
//        verify(favoriteRepository).delete(favorite);
//    }
//
//    @Test
//    void removeFromFavorites_notFound_throwsException() {
//        when(favoriteRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(FavoriteNotFoundException.class,
//                () -> favoriteService.removeFromFavorites(1L));
//    }
//
//    @Test
//    void getAllByUserId_returnsFavorites() {
//        List<Favorite> favorites = List.of(favorite);
//        when(favoriteRepository.findAllByUserId(1L)).thenReturn(favorites);
//
//        List<Favorite> result = favoriteService.getAllByUserId(1L);
//
//        assertEquals(1, result.size());
//        assertEquals(favorite, result.get(0));
//    }
//
//    @Test
//    void getFavoriteById_found() {
//        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
//
//        Favorite result = favoriteService.getFavoriteById(1L);
//
//        assertEquals(favorite, result);
//    }
//
//    @Test
//    void getFavoriteById_notFound_throwsException() {
//        when(favoriteRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(FavoriteNotFoundException.class,
//                () -> favoriteService.getFavoriteById(1L));
//    }
//
//    @Test
//    void updateFavorite_success() {
//        Favorite updatedFavorite = Favorite.builder()
//                .user(User.builder().id(3L).fullName("New User").build())
//                .product(Product.builder().id(4L).name("New Product").build())
//                .build();
//
//        Favorite existingFavorite = Favorite.builder()
//                .user(user)
//                .product(product)
//                .build();
//
//        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(existingFavorite));
//        when(userService.getUserById(3L)).thenReturn(updatedFavorite.getUser());
//        when(productService.getProductById(4L)).thenReturn(updatedFavorite.getProduct());
//        when(favoriteRepository.save(existingFavorite)).thenReturn(existingFavorite);
//
//        Favorite result = favoriteService.updateFavorite(1L, updatedFavorite);
//
//        assertEquals(updatedFavorite.getUser(), result.getUser());
//        assertEquals(updatedFavorite.getProduct(), result.getProduct());
//        verify(favoriteRepository).save(existingFavorite);
//    }
//}
