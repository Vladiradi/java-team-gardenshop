package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import java.util.List;

@RequestMapping("/v1/orders")
public interface OrderController {

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    List<OrderShortResponseDto> getAllForCurrentUser();

    @GetMapping("/history/delivered")
    @PreAuthorize("hasRole('USER')")
    List<OrderResponseDto> getAllDeliveredForCurrentUser();

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    List<OrderShortResponseDto> getAll();

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    OrderResponseDto getById(@PathVariable @Positive Long orderId);

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponseDto create(@RequestBody @Valid OrderCreateRequestDto orderCreateRequestDto);


    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{orderId}")
    OrderResponseDto delete(@PathVariable @Positive Long orderId);
}
