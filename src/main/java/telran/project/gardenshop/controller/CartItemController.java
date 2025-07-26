package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.CartItemRequestDto;
import telran.project.gardenshop.dto.CartItemResponseDto;
import telran.project.gardenshop.service.CartItemService;

import java.util.List;

@RestController
@RequestMapping("/api/carts/{cartId}/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @Operation(summary = "Add item to cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Cart or product not found")
    })
    @PostMapping
    public ResponseEntity<CartItemResponseDto> addItemToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemRequestDto requestDto) {
        CartItemResponseDto response = cartItemService.addItemToCart(cartId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all items in cart")
    @ApiResponse(responseCode = "200", description = "List of cart items retrieved")
    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(
            @PathVariable Long cartId) {
        List<CartItemResponseDto> items = cartItemService.getCartItems(cartId);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Update item quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity updated"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<CartItemResponseDto> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @RequestParam @Min(1) Integer quantity) {
        CartItemResponseDto response = cartItemService.updateItemQuantity(cartId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove item from cart")
    @ApiResponse(responseCode = "204", description = "Item removed")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear all items from cart")
    @ApiResponse(responseCode = "204", description = "Cart cleared")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}