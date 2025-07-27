package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderHistoryDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;
import telran.project.gardenshop.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final UserService userService;

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

    @Override
    public List<OrderHistoryDto> getMyOrderHistory() {
        User currentUser = userService.getCurrentUser();
        return orderService.getOrderHistory(currentUser.getEmail());
    }
}