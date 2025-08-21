package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {

    private final OrderService orderService;

    private final OrderMapper orderMapper;

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user order history", description = "Retrieve current user's order history")
    public List<OrderShortResponseDto> getAllForCurrentUser() {
        return orderService.getForCurrentUser()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/history/delivered")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get delivered orders", description = "Retrieve current user's delivered orders")
    public List<OrderResponseDto> getAllDeliveredForCurrentUser() {
        return orderService.getForCurrentUser()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(orderMapper::toDto)
                .toList();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all active orders", description = "Retrieve all active orders (Admin only)")
    public List<OrderShortResponseDto> getAll() {
        return orderService.getActive()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by unique identifier")
    public OrderResponseDto getById(@PathVariable @Positive Long orderId) {
        Order order = orderService.getById(orderId);
        return orderMapper.toDto(order);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new order", description = "Create a new order from current user's cart")
    public OrderResponseDto create(@RequestBody @Valid OrderCreateRequestDto orderCreateRequestDto) {
        var created = orderService.create(orderCreateRequestDto);
        return orderMapper.toDto(created);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cancel order", description = "Cancel an existing order")
    public OrderResponseDto delete(@PathVariable @Positive Long orderId) {
        var cancelled = orderService.cancel(orderId);
        return orderMapper.toDto(cancelled);
    }
}
