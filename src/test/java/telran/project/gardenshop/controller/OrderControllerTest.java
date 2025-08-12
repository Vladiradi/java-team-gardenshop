package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void createOrder_validInput_returnsCreated() throws Exception {
                OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                                .deliveryAddress("123 Test St")
                                .deliveryMethod(DeliveryMethod.COURIER)
                                .build();

                Order order = Order.builder()
                                .id(1L)
                                .deliveryAddress("123 Test St")
                                .status(OrderStatus.NEW)
                                .createdAt(LocalDateTime.now())
                                .build();

                OrderResponseDto responseDto = new OrderResponseDto();
                responseDto.setId(1L);
                responseDto.setAddress("123 Test St");

                when(orderService.create(any(OrderCreateRequestDto.class))).thenReturn(order);
                when(orderMapper.toDto(order)).thenReturn(responseDto);

                mockMvc.perform(post("/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.address").value("123 Test St"));
        }

        @Test
        void getOrderById_existingId_returnsOrder() throws Exception {
                Long orderId = 1L;

                Order order = Order.builder()
                                .id(orderId)
                                .deliveryAddress("123 Test St")
                                .status(OrderStatus.NEW)
                                .build();

                OrderResponseDto responseDto = new OrderResponseDto();
                responseDto.setId(orderId);
                responseDto.setAddress("123 Test St");

                when(orderService.getById(orderId)).thenReturn(order);
                when(orderMapper.toDto(order)).thenReturn(responseDto);

                mockMvc.perform(get("/v1/orders/{orderId}", orderId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(orderId))
                                .andExpect(jsonPath("$.address").value("123 Test St"));
        }

        @Test
        void getAllOrders_returnsList() throws Exception {
                Order order1 = Order.builder().id(1L).status(OrderStatus.NEW).build();
                Order order2 = Order.builder().id(2L).status(OrderStatus.PAID).build();

                List<Order> orders = List.of(order1, order2);

                OrderShortResponseDto dto1 = new OrderShortResponseDto();
                dto1.setId(1L);

                OrderShortResponseDto dto2 = new OrderShortResponseDto();
                dto2.setId(2L);

                when(orderService.getAll()).thenReturn(orders);
                when(orderMapper.toShortDto(order1)).thenReturn(dto1);
                when(orderMapper.toShortDto(order2)).thenReturn(dto2);

                mockMvc.perform(get("/v1/orders"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[1].id").value(2));
        }

        @Test
        void deleteOrder_existingId_returnsDeleted() throws Exception {
                Long orderId = 1L;

                Order deletedOrder = Order.builder()
                                .id(orderId)
                                .status(OrderStatus.CANCELLED)
                                .build();

                OrderResponseDto responseDto = new OrderResponseDto();
                responseDto.setId(orderId);

                when(orderService.cancel(orderId)).thenReturn(deletedOrder);
                when(orderMapper.toDto(deletedOrder)).thenReturn(responseDto);

                mockMvc.perform(delete("/v1/orders/{orderId}", orderId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(orderId));
        }
}
