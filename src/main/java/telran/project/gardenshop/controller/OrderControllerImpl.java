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
import telran.project.gardenshop.entity.User;
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
        var currentUser = userService.getCurrent();
        return orderService.getByUserId(currentUser.getId())
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getAllDeliveredForCurrentUser() {
        var currentUser = userService.getCurrent();
        return orderService.getByUserId(currentUser.getId())
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderShortResponseDto> getAll() {

        return orderService.getActive()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @Override
    public OrderResponseDto getById(@Positive Long orderId) {
        Order order = orderService.getById(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto create(@Valid OrderCreateRequestDto orderCreateRequestDto) {
        User currentUser = userService.getCurrent();
        Order created = orderService.createForCurrentUser(orderCreateRequestDto);
        return orderMapper.toDto(created);
    }

    @Override
    public OrderResponseDto delete(@Positive Long orderId) {
        // В твоём сервисе есть cancelOrder(orderId) (void). Делаем «мягкое удаление» = отмена и возвращаем изменённый заказ
        orderService.cancel(orderId);
        var cancelled = orderService.getById(orderId);
        return orderMapper.toDto(cancelled);
    }
}
