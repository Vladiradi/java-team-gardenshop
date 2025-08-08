package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;
import telran.project.gardenshop.service.UserService;
import telran.project.gardenshop.service.security.JwtFilter;
import telran.project.gardenshop.service.security.JwtService;
import telran.project.gardenshop.configuration.SecurityConfig;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testGetById() throws Exception {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .id(orderId)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(responseDto);

        mockMvc.perform(get("/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())));

        verify(orderService).getOrderById(orderId);
        verify(orderMapper).toDto(order);
    }

    @Test
    void testCreateOrder() throws Exception {
        Long userId = 1L;
        OrderCreateRequestDto createDto = new OrderCreateRequestDto();
        createDto.setDeliveryMethod(telran.project.gardenshop.enums.DeliveryMethod.COURIER);
        createDto.setAddress("123 Street");
        createDto.setContactName("John");
        createDto.setCreatedAt(LocalDateTime.now());

        Order order = new Order();
        order.setId(100L);

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .id(100L)
                .build();

        when(orderService.createOrder(eq(userId), any(OrderCreateRequestDto.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(responseDto);

        mockMvc.perform(post("/v1/orders/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)));

        verify(orderService).createOrder(eq(userId), any(OrderCreateRequestDto.class));
        verify(orderMapper).toDto(order);
    }
}

