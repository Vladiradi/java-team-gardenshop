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
import telran.project.gardenshop.dto.CartItemResponseDto;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.exception.CartNotFoundException;
import telran.project.gardenshop.mapper.CartMapper;
import telran.project.gardenshop.service.CartService;
import telran.project.gardenshop.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest extends AbstractTest {

        private MockMvc mockMvc;

        @Mock
        private CartService cartService;

        @Mock
        private UserService userService;

        @Mock
        private CartMapper cartMapper;

        @InjectMocks
        private CartController cartController;

        private ObjectMapper objectMapper;

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("GET /v1/cart - Get cart for current user")
        void getForCurrentUser() throws Exception {
                when(userService.getCurrent()).thenReturn(user1);
                when(cartService.get()).thenReturn(cart1);
                when(cartMapper.toDto(cart1)).thenReturn(cartResponseDto1);

                mockMvc.perform(get("/v1/cart"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper.writeValueAsString(cartResponseDto1)));
        }

        @Test
        @DisplayName("POST /v1/cart/items - Add cart item for current user")
        void addItem() throws Exception {
                Long productId = product1.getId();

                CartItem cartItemUpdated = CartItem.builder()
                                .id(cartItem1.getId())
                                .product(cartItem1.getProduct())
                                .quantity(cartItem1.getQuantity() + 1)
                                .build();

                Cart cartUpdated = Cart.builder()
                                .id(cart1.getId())
                                .user(cart1.getUser())
                                .items(cart1.getItems())
                                .build();

                cartUpdated.getItems().remove(cartItem1);
                cartUpdated.getItems().add(cartItemUpdated);

                CartItemResponseDto cartItemResponseDtoUpdated = CartItemResponseDto.builder()
                                .id(cartItemUpdated.getId())
                                .productId(cartItemUpdated.getProduct().getId())
                                .quantity(cartItemUpdated.getQuantity())
                                .build();

                CartResponseDto cartResponseDtoUpdated = new CartResponseDto();
                cartResponseDtoUpdated.setId(cartUpdated.getId());
                cartResponseDtoUpdated.setUserId(cartUpdated.getUser().getId());
                cartResponseDtoUpdated.setItems(cartUpdated.getItems().stream()
                                .map(item -> CartItemResponseDto.builder()
                                                .id(item.getId())
                                                .productId(item.getProduct().getId())
                                                .quantity(item.getQuantity())
                                                .build())
                                .toList());

                when(cartService.addItem(productId)).thenReturn(cartUpdated);
                when(cartMapper.toDto(cartUpdated)).thenReturn(cartResponseDtoUpdated);

                mockMvc.perform(post("/v1/cart/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("productId", String.valueOf(productId)))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper
                                                                .writeValueAsString(cartResponseDtoUpdated)));
        }

        @Test
        @DisplayName("PUT /v1/cart/items/{cartItemId} - Update cart item quantity")
        void updateItem() throws Exception {
                Long cartItemId = cartItem1.getId();
                Integer quantity = 3;

                CartItem cartItemUpdated = CartItem.builder()
                                .id(cartItem1.getId())
                                .product(cartItem1.getProduct())
                                .quantity(quantity)
                                .build();

                Cart cartUpdated = Cart.builder()
                                .id(cart1.getId())
                                .user(cart1.getUser())
                                .items(cart1.getItems())
                                .build();

                cartUpdated.getItems().remove(cartItem1);
                cartUpdated.getItems().add(cartItemUpdated);

                CartResponseDto cartResponseDtoUpdated = new CartResponseDto();
                cartResponseDtoUpdated.setId(cartUpdated.getId());
                cartResponseDtoUpdated.setUserId(cartUpdated.getUser().getId());
                cartResponseDtoUpdated.setItems(cartUpdated.getItems().stream()
                                .map(item -> CartItemResponseDto.builder()
                                                .id(item.getId())
                                                .productId(item.getProduct().getId())
                                                .quantity(item.getQuantity())
                                                .build())
                                .toList());

                when(cartService.updateItem(cartItemId, quantity)).thenReturn(cartUpdated);
                when(cartMapper.toDto(cartUpdated)).thenReturn(cartResponseDtoUpdated);

                mockMvc.perform(put("/v1/cart/items/{cartItemId}", cartItemId)
                                .param("quantity", quantity.toString()))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper
                                                                .writeValueAsString(cartResponseDtoUpdated)));
        }

        @Test
        @DisplayName("DELETE /v1/cart/items/{cartItemId} - Delete cart item successfully")
        void deleteItemPositiveCase() throws Exception {
                Long cartItemId = cartItem1.getId();

                Cart cartUpdated = Cart.builder()
                                .id(cart1.getId())
                                .user(cart1.getUser())
                                .items(cart1.getItems())
                                .build();
                cartUpdated.getItems().remove(cartItem1);

                CartResponseDto cartResponseDtoUpdated = new CartResponseDto();
                cartResponseDtoUpdated.setId(cartUpdated.getId());
                cartResponseDtoUpdated.setUserId(cartUpdated.getUser().getId());
                cartResponseDtoUpdated.setItems(cartUpdated.getItems().stream()
                                .map(item -> CartItemResponseDto.builder()
                                                .id(item.getId())
                                                .productId(item.getProduct().getId())
                                                .quantity(item.getQuantity())
                                                .build())
                                .toList());

                when(cartService.deleteItem(cartItemId)).thenReturn(cartUpdated);
                when(cartMapper.toDto(cartUpdated)).thenReturn(cartResponseDtoUpdated);

                mockMvc.perform(delete("/v1/cart/items/{cartItemId}", cartItemId))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper
                                                                .writeValueAsString(cartResponseDtoUpdated)));
        }

        @Test
        @DisplayName("DELETE /v1/cart/items/{cartItemId} - Delete cart item not found")
        void deleteItemNegativeCase() throws Exception {
                Long cartItemId = 99L;

                when(cartService.deleteItem(cartItemId)).thenThrow(new CartNotFoundException("Cart not found"));

                mockMvc.perform(delete("/v1/cart/items/{cartItemId}", cartItemId))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }
}
