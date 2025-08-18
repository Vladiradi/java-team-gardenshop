package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest extends AbstractTest {

        private MockMvc mockMvc;

        @Mock
        private FavoriteService favoriteService;

        @Mock
        private FavoriteMapper favoriteMapper;

        @InjectMocks
        private FavoriteController favoriteController;

        private ObjectMapper objectMapper;

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(favoriteController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("GET /v1/favorites - Get current user favorites")
        void getFavorites_shouldReturnList_usingBuilder() throws Exception {
                when(favoriteService.getCurrentUserFavorites()).thenReturn(List.of(favorite1, favorite2));
                when(favoriteMapper.toDto(favorite1)).thenReturn(favoriteResponseDto1);
                when(favoriteMapper.toDto(favorite2)).thenReturn(favoriteResponseDto2);

                mockMvc.perform(get("/v1/favorites"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$[0].id").value(favorite1.getId()),
                                                jsonPath("$[0].productId").value(favorite1.getProduct().getId()),
                                                jsonPath("$[1].id").value(favorite2.getId()),
                                                jsonPath("$[1].productId").value(favorite2.getProduct().getId()));
        }

        @Test
        @DisplayName("POST /v1/favorites - Add favorite successfully")
        void addFavorite_returnsCreated() throws Exception {
                FavoriteRequestDto request = new FavoriteRequestDto();
                request.setProductId(product3.getId());

                when(favoriteService.addToFavorites(any(Favorite.class))).thenReturn(favoriteToCreate);
                when(favoriteMapper.toDto(favoriteToCreate)).thenReturn(favoriteResponseCreatedDto);

                mockMvc.perform(post("/v1/favorites")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpectAll(
                                                status().isCreated(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper
                                                                .writeValueAsString(favoriteResponseCreatedDto)));
        }

        @Test
        @DisplayName("DELETE /v1/favorites/{favoriteId} - Remove favorite successfully")
        void removeFavorite_returnsNoContent() throws Exception {
                Long favoriteId = favorite1.getId();

                mockMvc.perform(delete("/v1/favorites/{favoriteId}", favoriteId))
                                .andDo(print())
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("GET /v1/favorites/{favoriteId} - Get favorite by ID")
        void getFavoriteById_returnsFavorite() throws Exception {
                Long favoriteId = favorite1.getId();

                when(favoriteService.getFavoriteById(favoriteId)).thenReturn(favorite1);
                when(favoriteMapper.toDto(favorite1)).thenReturn(favoriteResponseDto1);

                mockMvc.perform(get("/v1/favorites/{favoriteId}", favoriteId))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper.writeValueAsString(favoriteResponseDto1)));
        }
}
