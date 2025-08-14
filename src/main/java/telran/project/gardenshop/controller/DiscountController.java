package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.DiscountRequestDto;
import telran.project.gardenshop.dto.DiscountResponseDto;
import telran.project.gardenshop.dto.ProductOfDayDto;
import telran.project.gardenshop.enums.DiscountType;
import telran.project.gardenshop.service.DiscountService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    @Operation(summary = "Create a new discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Discount created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<DiscountResponseDto> createDiscount(@Valid @RequestBody DiscountRequestDto requestDto) {
        DiscountResponseDto discount = discountService.createDiscount(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(discount);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get discount by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount found"),
            @ApiResponse(responseCode = "404", description = "Discount not found")
    })
    public ResponseEntity<DiscountResponseDto> getDiscountById(@PathVariable Long id) {
        DiscountResponseDto discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(discount);
    }

    @GetMapping
    @Operation(summary = "Get all active discounts")
    public ResponseEntity<List<DiscountResponseDto>> getAllActiveDiscounts() {
        List<DiscountResponseDto> discounts = discountService.getAllActiveDiscounts();
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get discounts by type")
    public ResponseEntity<List<DiscountResponseDto>> getDiscountsByType(
            @PathVariable @Parameter(description = "Discount type", schema = @Schema(allowableValues = { "PERCENTAGE",
                    "FIXED_AMOUNT", "PRODUCT_OF_DAY" })) DiscountType type) {
        List<DiscountResponseDto> discounts = discountService.getDiscountsByType(type);
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get discounts for specific product")
    public ResponseEntity<List<DiscountResponseDto>> getDiscountsByProductId(@PathVariable Long productId) {
        List<DiscountResponseDto> discounts = discountService.getDiscountsByProductId(productId);
        return ResponseEntity.ok(discounts);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Discount not found")
    })
    public ResponseEntity<DiscountResponseDto> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequestDto requestDto) {
        DiscountResponseDto discount = discountService.updateDiscount(id, requestDto);
        return ResponseEntity.ok(discount);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discount deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Discount not found")
    })
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discount deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Discount not found")
    })
    public ResponseEntity<Void> deactivateDiscount(@PathVariable Long id) {
        discountService.deactivateDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products-with-discounts")
    @Operation(summary = "Get all products with active discounts")
    public ResponseEntity<List<DiscountResponseDto>> getProductsWithDiscounts() {
        List<DiscountResponseDto> products = discountService.getProductsWithDiscounts();
        return ResponseEntity.ok(products);
    }

    // Товар дня endpoints
    @GetMapping("/product-of-day")
    @Operation(summary = "Get product of the day")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product of day found"),
            @ApiResponse(responseCode = "404", description = "No product of day currently active")
    })
    public ResponseEntity<ProductOfDayDto> getProductOfDay() {
        ProductOfDayDto productOfDay = discountService.getProductOfDay();
        return ResponseEntity.ok(productOfDay);
    }

    @PostMapping("/product-of-day")
    @Operation(summary = "Set product of the day")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product of day set successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductOfDayDto> setProductOfDay(
            @RequestParam Long productId,
            @RequestParam(required = false) String description) {
        ProductOfDayDto productOfDay = discountService.setProductOfDay(productId, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(productOfDay);
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if product has active discounts")
    public ResponseEntity<Boolean> hasActiveDiscounts(@PathVariable Long productId) {
        boolean hasDiscounts = discountService.hasActiveDiscounts(productId);
        return ResponseEntity.ok(hasDiscounts);
    }
}
