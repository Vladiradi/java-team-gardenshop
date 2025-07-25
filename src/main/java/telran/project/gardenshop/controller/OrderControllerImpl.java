package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderShortResponseDto> getAll(Long userId) {
        return orderService.getOrdersByUserId(userId)
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @Override
    public OrderResponseDto getById(Long orderId) {
        return orderMapper.toDto(orderService.getOrderById(orderId));
    }

    @Override
    public OrderResponseDto create(Long userId, @Valid OrderCreateRequestDto dto) {
        return orderMapper.toDto(orderService.createOrder(userId, dto));
    }

    @Override
    public OrderResponseDto addOrderItem(Long orderId, Long productId, Integer quantity) {
        return orderMapper.toDto(orderService.addItem(orderId, productId, quantity));
    }

    @Override
    public OrderResponseDto updateOrderItem(Long orderItemId, Integer quantity) {
        return orderMapper.toDto(orderService.updateItem(orderItemId, quantity));
    }

    @Override
    public OrderResponseDto removeOrderItem(Long orderItemId) {
        return orderMapper.toDto(orderService.removeItem(orderItemId));
    }

    @Override
    public void delete(Long orderId) {
        orderService.deleteOrder(orderId);
    }
}