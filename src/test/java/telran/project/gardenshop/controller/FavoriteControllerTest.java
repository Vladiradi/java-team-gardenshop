package telran.project.gardenshop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;
import telran.project.gardenshop.service.security.JwtService;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    FavoriteService favoriteService;

    @MockBean
    FavoriteMapper favoriteMapper;

    @MockBean
    JwtService jwtService;

    @Test
    void getFavorites_shouldReturnList_usingBuilder() throws Exception {
        Favorite favorite1 = Favorite.builder()
                .id(101L)
                .user(User.builder().id(1L).build())
                .product(Product.builder().id(11L).build())
                .build();

        Favorite favorite2 = Favorite.builder()
                .id(102L)
                .user(User.builder().id(1L).build())
                .product(Product.builder().id(12L).build())
                .build();

        List<Favorite> favorites = List.of(favorite1, favorite2);

        FavoriteResponseDto dto1 = FavoriteResponseDto.builder()
                .id(101L).userId(1L).productId(11L)
                .productName("Product 11").price(50.0).imageUrl("img11.jpg").build();

        FavoriteResponseDto dto2 = FavoriteResponseDto.builder()
                .id(102L).userId(1L).productId(12L)
                .productName("Product 12").price(75.0).imageUrl("img12.jpg").build();

        when(favoriteService.getCurrentUserFavorites()).thenReturn(favorites);
        when(favoriteMapper.toDto(favorite1)).thenReturn(dto1);
        when(favoriteMapper.toDto(favorite2)).thenReturn(dto2);

        mockMvc.perform(get("/v1/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].productName").value("Product 11"))
                .andExpect(jsonPath("$[1].id").value(102))
                .andExpect(jsonPath("$[1].productName").value("Product 12"));
    }

    @Test
    void addFavorite_returnsCreated() throws Exception {
        FavoriteRequestDto request = new FavoriteRequestDto();
        request.setUserId(1L);
        request.setProductId(10L);

        Favorite savedFavorite = Favorite.builder()
                .id(100L)
                .user(User.builder().id(1L).build())
                .product(Product.builder().id(10L).build())
                .build();

        FavoriteResponseDto responseDto = new FavoriteResponseDto(
                100L, 1L, 10L, "Product name", 99.99, "image.jpg"
        );

        when(favoriteService.addToFavorites(any(Favorite.class))).thenReturn(savedFavorite);
        when(favoriteMapper.toDto(savedFavorite)).thenReturn(responseDto);

        mockMvc.perform(post("/v1/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.productId").value(10))
                .andExpect(jsonPath("$.productName").value("Product name"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.imageUrl").value("image.jpg"));
    }
}
