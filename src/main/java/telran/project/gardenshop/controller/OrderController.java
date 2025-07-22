package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;

import java.util.List;

@RequestMapping("/v1/orders")
@SecurityRequirement(name = "bearerAuth")
public interface OrderController {

    @GetMapping("/history/{userId}")
    List<OrderShortResponseDto> getAll(@PathVariable Long userId);

    @GetMapping("/{orderId}")
    OrderResponseDto getById(@PathVariable @Positive Long orderId);

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponseDto create(@PathVariable Long userId, @RequestBody @Valid OrderCreateRequestDto orderCreateRequestDto);

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponseDto addOrderItem(@RequestParam @Positive Long orderId,
                                  @RequestParam @Positive Long productId,
                                  @RequestParam @Positive Integer quantity);

    @PutMapping("/items")
    @ResponseStatus(HttpStatus.ACCEPTED)
    OrderResponseDto updateOrderItem(@RequestParam @Positive Long orderItemId,
                                     @RequestParam @Positive Integer quantity);

    @DeleteMapping("/items/{orderItemId}")
    OrderResponseDto removeOrderItem(@PathVariable @Positive Long orderItemId);

    @DeleteMapping("/{orderId}")
    void delete(@PathVariable @Positive Long orderId);
}
