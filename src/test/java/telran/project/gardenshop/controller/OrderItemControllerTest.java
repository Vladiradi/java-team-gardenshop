package telran.project.gardenshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.mapper.OrderItemMapper;
import telran.project.gardenshop.service.OrderItemService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemControllerTest {

    @InjectMocks
    private OrderItemController controller;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderItemMapper orderItemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrderItem_shouldReturnCreatedResponse() {
        OrderItemRequestDto requestDto = new OrderItemRequestDto();
        OrderItem orderItem = new OrderItem();
        OrderItem savedItem = new OrderItem();
        OrderItemResponseDto responseDto = new OrderItemResponseDto();

        when(orderItemMapper.toEntity(requestDto)).thenReturn(orderItem);
        when(orderItemService.createOrderItem(orderItem)).thenReturn(savedItem);
        when(orderItemMapper.toDto(savedItem)).thenReturn(responseDto);

        ResponseEntity<OrderItemResponseDto> result = controller.createOrderItem(requestDto);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(responseDto, result.getBody());

        verify(orderItemMapper).toEntity(requestDto);
        verify(orderItemService).createOrderItem(orderItem);
        verify(orderItemMapper).toDto(savedItem);
    }

    @Test
    void getOrderItemById_shouldReturnOrderItem() {
        Long id = 1L;
        OrderItem orderItem = new OrderItem();
        OrderItemResponseDto dto = new OrderItemResponseDto();

        when(orderItemService.getOrderItemById(id)).thenReturn(orderItem);
        when(orderItemMapper.toDto(orderItem)).thenReturn(dto);

        ResponseEntity<OrderItemResponseDto> result = controller.getOrderItemById(id);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(dto, result.getBody());

        verify(orderItemService).getOrderItemById(id);
        verify(orderItemMapper).toDto(orderItem);
    }

    @Test
    void deleteOrderItem_shouldReturnNoContent() {
        Long id = 1L;

        ResponseEntity<Void> result = controller.deleteOrderItem(id);

        assertEquals(204, result.getStatusCodeValue());
        verify(orderItemService).deleteOrderItem(id);
    }

    @Test
    void getItemsByOrderId_shouldReturnListOfOrderItems() {
        Long orderId = 5L;
        List<OrderItem> items = Arrays.asList(new OrderItem(), new OrderItem());
        List<OrderItemResponseDto> dtos = Arrays.asList(new OrderItemResponseDto(), new OrderItemResponseDto());

        when(orderItemService.getAllByOrderId(orderId)).thenReturn(items);
        when(orderItemMapper.toDto(any())).thenReturn(dtos.get(0), dtos.get(1));

        ResponseEntity<List<OrderItemResponseDto>> result = controller.getItemsByOrderId(orderId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(dtos, result.getBody());

        verify(orderItemService).getAllByOrderId(orderId);
        verify(orderItemMapper, times(2)).toDto(any());
    }
}
