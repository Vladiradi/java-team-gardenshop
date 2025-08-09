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
import telran.project.gardenshop.dto.CartItemRequestDto;
import telran.project.gardenshop.dto.CartItemResponseDto;
import telran.project.gardenshop.service.CartItemService;
import telran.project.gardenshop.service.security.JwtService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartItemRequestDto requestDto;
    private CartItemResponseDto responseDto;
    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        requestDto = new CartItemRequestDto(2L, 3, 100.0);

        responseDto = CartItemResponseDto.builder()
                .id(1L)
                .cartId(1L)
                .productId(2L)
                .quantity(3)
                .price(100.0)
                .productName("Test Product")
                .build();
    }

    @Test
    void addItemToCart_ReturnsCreated() throws Exception {
        when(cartItemService.addItemToCart(eq(1L), any(CartItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/v1/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.cartId").value(responseDto.getCartId()))
                .andExpect(jsonPath("$.productId").value(responseDto.getProductId()))
                .andExpect(jsonPath("$.quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.price").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.productName").value(responseDto.getProductName()));

        verify(cartItemService).addItemToCart(eq(1L), any(CartItemRequestDto.class));
    }

    @Test
    void getCartItems_ReturnsOk() throws Exception {
        List<CartItemResponseDto> items = List.of(responseDto);
        when(cartItemService.getCartItems(1L)).thenReturn(items);

        mockMvc.perform(get("/v1/carts/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()))
                .andExpect(jsonPath("$[0].id").value(responseDto.getId()));

        verify(cartItemService).getCartItems(1L);
    }

    @Test
    void updateItemQuantity_ReturnsOk() throws Exception {
        when(cartItemService.updateItemQuantity(1L, 2L, 5)).thenReturn(responseDto);

        mockMvc.perform(put("/v1/carts/1/items/2")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(cartItemService).updateItemQuantity(1L, 2L, 5);
    }

    @Test
    void removeItemFromCart_ReturnsNoContent() throws Exception {
        doNothing().when(cartItemService).removeItemFromCart(1L, 2L);

        mockMvc.perform(delete("/v1/carts/1/items/2"))
                .andExpect(status().isNoContent());

        verify(cartItemService).removeItemFromCart(1L, 2L);
    }

    @Test
    void clearCart_ReturnsNoContent() throws Exception {
        doNothing().when(cartItemService).clearCart(1L);

        mockMvc.perform(delete("/v1/carts/1/items"))
                .andExpect(status().isNoContent());

        verify(cartItemService).clearCart(1L);
    }
}
