//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import telran.project.gardenshop.entity.Favorite;
//import telran.project.gardenshop.entity.Product;
//import telran.project.gardenshop.entity.User;
//import telran.project.gardenshop.exception.FavoriteAlreadyExistsException;
//import telran.project.gardenshop.exception.FavoriteNotFoundException;
//import telran.project.gardenshop.repository.FavoriteRepository;
//import telran.project.gardenshop.service.FavoriteServiceImpl;
//import telran.project.gardenshop.service.ProductService;
//import telran.project.gardenshop.service.UserService;
//
//import java.util.Optional;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class FavoriteServiceTest {
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
//    private AutoCloseable closeable;
//
//    private telran.project.gardenshop.entity.User user;
//    private Product product;
//    private Favorite favorite;
//
//    @BeforeEach
//    void setUp() {
//        closeable = MockitoAnnotations.openMocks(this);
//
//        user = User.builder().id(1L).fullName("Test User").build();
//        product = Product.builder().id(2L).name("Test Product").build();
//        favorite = Favorite.builder().user(user).product(product).build();
//    }
//
//    @Test
//    void addToFavorites_success() {
//        // given
//        when(userService.getUserById(1L)).thenReturn(user);
//        when(productService.getProductById(2L)).thenReturn(product);
//        when(favoriteRepository.findByUserIdAndProductId(1L, 2L)).thenReturn(Optional.empty());
//        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
//
//        // when
//        Favorite result = favoriteService.addToFavorites(favorite);
//
//        // then
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
//        when(favoriteRepository.findByUserIdAndProductId(1L, 2L))
//                .thenReturn(Optional.of(favorite));
//
//        assertThrows(FavoriteAlreadyExistsException.class,
//                () -> favoriteService.addToFavorites(favorite));
//    }

//    @Test
//    void removeFromFavorites_success() {
//        when(favoriteRepository.existsByUserIdAndProductId(2L, 1L)).thenReturn(true);
//
//        favoriteService.removeFromFavorites(1L, 2L);
//
//        verify(favoriteRepository).deleteByUserIdAndProductId(2L, 1L);
//    }
//
//    @Test
//    void removeFromFavorites_notFound_throwsException() {
//        when(favoriteRepository.existsByUserIdAndProductId(2L, 1L)).thenReturn(false);
//
//        assertThrows(FavoriteNotFoundException.class,
//                () -> favoriteService.removeFromFavorites(2L, 1L));
//    }




// use it  ------------------------
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
//    @AfterEach
//    void tearDown() throws Exception {
//        closeable.close();
//    }
//}
