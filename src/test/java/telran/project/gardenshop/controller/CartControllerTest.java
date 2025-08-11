package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.mapper.CartMapper;
import telran.project.gardenshop.service.CartService;
import telran.project.gardenshop.service.security.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private CartMapper cartMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    private Cart cart;
    private CartResponseDto cartDto;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id(1L)
                .build();

        cartDto = new CartResponseDto();
        cartDto.setId(1L);
    }

    @Test
    void getCurrentCart_ReturnsOk() throws Exception {
        when(cartService.get()).thenReturn(cart);
        when(cartMapper.toDto(cart)).thenReturn(cartDto);

        mockMvc.perform(get("/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartDto.getId()));

        verify(cartService).get();
        verify(cartMapper).toDto(cart);
    }

    @Test
    void addItem_ReturnsOk() throws Exception {
        Long productId = 2L;

        when(cartService.addItem(productId)).thenReturn(cart);
        when(cartMapper.toDto(cart)).thenReturn(cartDto);

        mockMvc.perform(post("/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("productId", String.valueOf(productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartDto.getId()));

        verify(cartService).addItem(eq(productId));
        verify(cartMapper).toDto(cart);
    }

    @Test
    void updateItem_ReturnsOk() throws Exception {
        Long cartItemId = 5L;
        Integer quantity = 3;

        when(cartService.updateItem(cartItemId, quantity)).thenReturn(cart);
        when(cartMapper.toDto(cart)).thenReturn(cartDto);

        mockMvc.perform(put("/v1/cart/items/{cartItemId}", cartItemId)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartDto.getId()));

        verify(cartService).updateItem(eq(cartItemId), eq(quantity));
        verify(cartMapper).toDto(cart);
    }

    @Test
    void deleteItem_ReturnsNoContent() throws Exception {
        Long cartItemId = 7L;

        doNothing().when(cartService).deleteItem(cartItemId);

        mockMvc.perform(delete("/v1/cart/items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent());

        verify(cartService).deleteItem(eq(cartItemId));
        verifyNoInteractions(cartMapper);
    }
}
