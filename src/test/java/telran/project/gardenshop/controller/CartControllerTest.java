package telran.project.gardenshop.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.service.CartService;
import telran.project.gardenshop.service.security.JwtService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtService jwtService;

    @Test
    void addToCart_ReturnsCartResponse() throws Exception {
        Long userId = 1L;
        CartResponseDto responseDto = new CartResponseDto();
        responseDto.setId(10L);
        responseDto.setUserId(userId);

        when(cartService.addToCart(userId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/carts/add/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(userId));
    }
}
