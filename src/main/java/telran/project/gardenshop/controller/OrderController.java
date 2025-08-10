package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/v1/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapper;

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public List<OrderShortResponseDto> getAllForCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return orderService.getOrdersByUserId(currentUser.getId())
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/history/delivered")
    @PreAuthorize("hasRole('USER')")
    public List<OrderResponseDto> getAllDeliveredForCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return orderService.getOrdersByUserId(currentUser.getId())
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(orderMapper::toDto)
                .toList();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderShortResponseDto> getAll() {
        return orderService.getActiveOrders()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto getById(@PathVariable @Positive Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return orderMapper.toDto(order);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@RequestBody @Valid OrderCreateRequestDto orderCreateRequestDto) {
        var currentUser = userService.getCurrentUser();
        var created = orderService.createOrder(currentUser.getId(), orderCreateRequestDto);
        return orderMapper.toDto(created);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto addItem(@RequestParam @Positive Long orderId,
                                    @RequestParam @Positive Long productId,
                                    @RequestParam @Positive Integer quantity) {
        var updated = orderService.addItem(orderId, productId, quantity);
        return orderMapper.toDto(updated);
    }

    @PutMapping("/items")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponseDto updateItem(@RequestParam @Positive Long orderItemId,
                                       @RequestParam @Positive Integer quantity) {
        var updated = orderService.updateItem(orderItemId, quantity);
        return orderMapper.toDto(updated);
    }

    @DeleteMapping("/items/{orderItemId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto removeItem(@PathVariable @Positive Long orderItemId) {
        var updated = orderService.removeItem(orderItemId);
        return orderMapper.toDto(updated);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto delete(@PathVariable @Positive Long orderId) {
        orderService.cancelOrder(orderId);
        var cancelled = orderService.getOrderById(orderId);
        return orderMapper.toDto(cancelled);
    }
}
