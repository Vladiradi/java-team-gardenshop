package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;
import telran.project.gardenshop.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderShortResponseDto> getAllForCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return orderService.getOrdersByUserId(currentUser.getId())
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getAllDeliveredForCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return orderService.getOrdersByUserId(currentUser.getId())
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderShortResponseDto> getAll() {

        return orderService.getActiveOrders()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @Override
    public OrderResponseDto getById(@Positive Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto create(@Valid OrderCreateRequestDto orderCreateRequestDto) {
        var currentUser = userService.getCurrentUser();
        var created = orderService.createOrder(currentUser.getId(), orderCreateRequestDto);
        return orderMapper.toDto(created);
    }

    @Override
    public OrderResponseDto addItem(@Positive Long orderId,
                                    @Positive Long productId,
                                    @Positive Integer quantity) {
        var updated = orderService.addItem(orderId, productId, quantity);
        return orderMapper.toDto(updated);
    }

    @Override
    public OrderResponseDto updateItem(@Positive Long orderItemId,
                                       @Positive Integer quantity) {
        var updated = orderService.updateItem(orderItemId, quantity);
        return orderMapper.toDto(updated);
    }

    @Override
    public OrderResponseDto removeItem(@Positive Long orderItemId) {
        var updated = orderService.removeItem(orderItemId);
        return orderMapper.toDto(updated);
    }

    @Override
    public OrderResponseDto delete(@Positive Long orderId) {
        // В твоём сервисе есть cancelOrder(orderId) (void). Делаем «мягкое удаление» = отмена и возвращаем изменённый заказ
        orderService.cancelOrder(orderId);
        var cancelled = orderService.getOrderById(orderId);
        return orderMapper.toDto(cancelled);
    }
}