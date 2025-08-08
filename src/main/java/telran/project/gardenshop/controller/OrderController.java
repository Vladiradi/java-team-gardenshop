package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/v1/orders")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final UserService userService;

    @GetMapping("/history/{userId}")
    public List<OrderShortResponseDto> getAll(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId)
                .stream()
                .map(orderMapper::toShortDto)
                .toList();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getById(@PathVariable @Positive Long orderId) {
        return orderMapper.toDto(orderService.getOrderById(orderId));
    }

    @GetMapping("/history")
    public List<OrderHistoryDto> getMyOrderHistory() {
        User currentUser = userService.getCurrentUser();
        var orders = orderService.getOrdersByUserId(currentUser.getId());
        return orders.stream()
                .map(this::toOrderHistoryDto)
                .toList();
    }

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@PathVariable Long userId, @RequestBody @Valid OrderCreateRequestDto dto) {
        return orderMapper.toDto(orderService.createOrder(userId, dto));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto addOrderItem(@RequestParam @Positive Long orderId,
                                         @RequestParam @Positive Long productId,
                                         @RequestParam @Positive Integer quantity) {
        return orderMapper.toDto(orderService.addItem(orderId, productId, quantity));
    }

    @PutMapping("/items")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponseDto updateOrderItem(@RequestParam @Positive Long orderItemId,
                                            @RequestParam @Positive Integer quantity) {
        return orderMapper.toDto(orderService.updateItem(orderItemId, quantity));
    }

    @DeleteMapping("/items/{orderItemId}")
    public OrderResponseDto removeOrderItem(@PathVariable @Positive Long orderItemId) {
        return orderMapper.toDto(orderService.removeItem(orderItemId));
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long orderId) {
        orderService.deleteOrder(orderId);
    }

    private OrderHistoryDto toOrderHistoryDto(telran.project.gardenshop.entity.Order order) {
        var products = order.getItems().stream()
                .map(item -> telran.project.gardenshop.dto.OrderItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice().doubleValue())
                        .build())
                .toList();

        return telran.project.gardenshop.dto.OrderHistoryDto.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalPrice(order.getItems().stream()
                        .map(i -> i.getPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity())))
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                        .doubleValue())
                .createdAt(order.getCreatedAt().toString())
                .products(products)
                .deliveryAddress(order.getDeliveryAddress())
                .recipientName(order.getContactName())
                .recipientPhone(userService.getUserById(order.getUser().getId()).getPhoneNumber())
                .build();
    }
}