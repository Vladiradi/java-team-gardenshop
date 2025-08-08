package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import telran.project.gardenshop.swagger.SwaggerResponses;
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
@RequestMapping("/v1/carts/{cartId}/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @Operation(summary = "Add item to cart")
    @SwaggerResponses.CartItemCreated
    @PostMapping
    public ResponseEntity<CartItemResponseDto> addItemToCart(

            @PathVariable Long cartId,

            @Valid @RequestBody CartItemRequestDto requestDto) {
        CartItemResponseDto response = cartItemService.addItemToCart(cartId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all items in cart")
    @SwaggerResponses.CartItemListRetrieved
    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(
            @PathVariable Long cartId) {
        List<CartItemResponseDto> items = cartItemService.getCartItems(cartId);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Update item quantity")
    @SwaggerResponses.CartItemUpdated
    @PutMapping("/{productId}")
    public ResponseEntity<CartItemResponseDto> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @RequestParam @Min(1) Integer quantity) {
        CartItemResponseDto response = cartItemService.updateItemQuantity(cartId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove item from cart")
    @SwaggerResponses.CartItemRemoved
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear all items from cart")
    @SwaggerResponses.CartCleared
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}