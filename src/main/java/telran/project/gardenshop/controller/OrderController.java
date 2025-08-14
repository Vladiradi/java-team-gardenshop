package telran.project.gardenshop.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.mapper.OrderMapper;
import telran.project.gardenshop.service.OrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    private final OrderMapper orderMapper;

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public List<OrderShortResponseDto> getAllForCurrentUser() {
        return orderService.getForCurrentUser()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/history/delivered")
    @PreAuthorize("hasRole('USER')")
    public List<OrderResponseDto> getAllDeliveredForCurrentUser() {
        return orderService.getForCurrentUser()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(orderMapper::toDto)
                .toList();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderShortResponseDto> getAll() {
        return orderService.getActive()
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto getById(@PathVariable @Positive Long orderId) {
        Order order = orderService.getById(orderId);
        return orderMapper.toDto(order);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@RequestBody @Valid OrderCreateRequestDto orderCreateRequestDto) {
        var created = orderService.create(orderCreateRequestDto);
        return orderMapper.toDto(created);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto delete(@PathVariable @Positive Long orderId) {
        var cancelled = orderService.cancel(orderId);
        return orderMapper.toDto(cancelled);
    }
}
