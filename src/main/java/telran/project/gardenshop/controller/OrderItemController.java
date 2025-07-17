package telran.project.gardenshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.mapper.OrderItemMapper;
import telran.project.gardenshop.service.OrderItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;
    private final OrderItemMapper orderItemMapper;

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> createOrderItem(@RequestBody OrderItemRequestDto dto) {
        // Преобразуем dto в entity
        OrderItem orderItem = orderItemMapper.toEntity(dto);
        // Создаем OrderItem
        OrderItem created = orderItemService.createOrderItem(orderItem);
        // Преобразуем entity обратно в dto для ответа
        OrderItemResponseDto responseDto = orderItemMapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItemMapper.toDto(orderItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponseDto>> getItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> items = orderItemService.getAllByOrderId(orderId);
        List<OrderItemResponseDto> dtos = items.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
