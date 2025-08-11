package telran.project.gardenshop.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.CartResponseDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.mapper.CartMapper;
import telran.project.gardenshop.service.CartService;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @Operation(summary = "Get current user's cart",
            description = "Returns the current user's shopping cart. If there is no cart - creates an empty one")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "cart get",
                    content = @Content(schema = @Schema(implementation = CartResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<CartResponseDto> getCurrentCart() {
        Cart cart = cartService.get();
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }

    @Operation(summary = "Add item to current cart",
            description = "adds 1 item by productId to the current user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added",
                    content = @Content(schema = @Schema(implementation = CartResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(@RequestParam Long productId) {
        Cart updated = cartService.addItem(productId);
        return ResponseEntity.status(HttpStatus.OK).body(cartMapper.toDto(updated));
    }

    @Operation(summary = "Update quantity of cart item",
            description = "Изменяет количество позиции корзины по cartItemId.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity updated",
                    content = @Content(schema = @Schema(implementation = CartResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateItem(
            @PathVariable Long cartItemId,
            @RequestParam @Min(1) Integer quantity) {
        Cart updated = cartService.updateItem(cartItemId, quantity);
        return ResponseEntity.ok(cartMapper.toDto(updated));
    }

    @Operation(summary = "Remove cart item",
            description = "Remove cart item with cartItemId.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removed"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long cartItemId) {
        cartService.deleteItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
