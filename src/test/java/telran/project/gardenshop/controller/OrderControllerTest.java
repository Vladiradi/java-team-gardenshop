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
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest extends AbstractTest {

        private MockMvc mockMvc;

        @Mock
        private OrderService orderService;

        @Mock
        private OrderMapper orderMapper;

        @InjectMocks
        private OrderController orderController;

        private ObjectMapper objectMapper;

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("GET /v1/orders/{orderId} - Get order by ID")
        void testGetById() throws Exception {
                Long orderId = 1L;

                when(orderService.getById(orderId)).thenReturn(order1);
                when(orderMapper.toDto(order1)).thenReturn(orderResponseDto1);

                mockMvc.perform(get("/v1/orders/{orderId}", orderId))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper.writeValueAsString(orderResponseDto1)));

                verify(orderService).getById(orderId);
                verify(orderMapper).toDto(order1);
        }

        @Test
        @DisplayName("POST /v1/orders - Create order successfully")
        void testCreateOrder() throws Exception {
                when(orderService.create(any(OrderCreateRequestDto.class))).thenReturn(order1);
                when(orderMapper.toDto(order1)).thenReturn(orderResponseDto1);

                mockMvc.perform(post("/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderCreateRequestDto)))
                                .andDo(print())
                                .andExpectAll(
                                                status().isCreated(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper.writeValueAsString(orderResponseDto1)));
        }

        @Test
        @DisplayName("GET /v1/orders/history - Get order history for current user")
        void testGetOrderHistory() throws Exception {
                when(orderService.getForCurrentUser()).thenReturn(List.of(order1, order3));
                when(orderMapper.toShortDto(order1)).thenReturn(orderShortResponseDto1);
                when(orderMapper.toShortDto(order3)).thenReturn(orderShortResponseDto3);

                mockMvc.perform(get("/v1/orders/history"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$[0].id").value(order1.getId()),
                                                jsonPath("$[1].id").value(order3.getId()));
        }

        @Test
        @DisplayName("GET /v1/orders - Get all active orders (admin)")
        void testGetAllActiveOrders() throws Exception {
                when(orderService.getActive()).thenReturn(List.of(order1, order2));
                when(orderMapper.toShortDto(order1)).thenReturn(orderShortResponseDto1);
                when(orderMapper.toShortDto(order2)).thenReturn(orderShortResponseDto2);

                mockMvc.perform(get("/v1/orders"))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                jsonPath("$[0].id").value(order1.getId()),
                                                jsonPath("$[1].id").value(order2.getId()));
        }

        @Test
        @DisplayName("DELETE /v1/orders/{orderId} - Cancel order successfully")
        void testCancelOrder() throws Exception {
                Long orderId = 1L;

                when(orderService.cancel(orderId)).thenReturn(order1);
                when(orderMapper.toDto(order1)).thenReturn(orderResponseDto1);

                mockMvc.perform(delete("/v1/orders/{orderId}", orderId))
                                .andDo(print())
                                .andExpectAll(
                                                status().isOk(),
                                                content().contentType(MediaType.APPLICATION_JSON),
                                                content().json(objectMapper.writeValueAsString(orderResponseDto1)));
        }
}
