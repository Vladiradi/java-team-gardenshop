package telran.project.gardenshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;
import telran.project.gardenshop.service.security.JwtService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)  // Отключаем security для тестов
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private FavoriteMapper favoriteMapper;

    @MockBean
    private JwtService jwtService;

    private User user = User.builder().id(1L).fullName("Test User").build();
    private Product product = Product.builder().id(2L).name("Test Product").build();
    private Favorite favorite;
    private FavoriteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        favorite = Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        responseDto = FavoriteResponseDto.builder()
                .userId(user.getId())
                .productId(product.getId())
                .build();
    }

    @Test
    void addFavorite_success() throws Exception {
        FavoriteRequestDto requestDto = new FavoriteRequestDto();
        requestDto.setUserId(1L);
        requestDto.setProductId(2L);

        when(favoriteService.addToFavorites(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toDto(any(Favorite.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": 1,
                                "productId": 2
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.productId").value(2));
    }

    @Test
    void removeFavorite_success() throws Exception {
        doNothing().when(favoriteService).removeFromFavorites(1L);

        mockMvc.perform(delete("/api/favorites/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(favoriteService).removeFromFavorites(1L);
    }

    @Test
    void getAllFavoritesByUser_success() throws Exception {
        List<Favorite> favorites = List.of(favorite);
        List<FavoriteResponseDto> dtos = List.of(responseDto);

        when(favoriteService.getAllByUserId(1L)).thenReturn(favorites);
        when(favoriteMapper.toDto(any(Favorite.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/favorites/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].productId").value(2));
    }
}
