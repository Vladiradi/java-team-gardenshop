package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import telran.project.gardenshop.utilities.ProductSpecification;
import telran.project.gardenshop.repository.ProductRepository;
import telran.project.gardenshop.dto.ProductDiscountDto;
import telran.project.gardenshop.dto.ProductEditDto;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.ProductMapper;
import telran.project.gardenshop.service.ProductService;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Products", description = "Product management operations")
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Add new product", description = "Create a new product in the catalog")
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto dto) {
        Product entity = productMapper.toEntity(dto);
        Product saved = productService.create(entity);
        return ResponseEntity.status(201).body(productMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve product information by unique identifier")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all available products")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<Product> products = productService.getAll();
        List<ProductResponseDto> dtoList = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Modify existing product information (title, description, price)")
    public ResponseEntity<ProductResponseDto> editProduct(
            @PathVariable Long id,
            @RequestBody ProductEditDto dto) {

        Product updated = productService.update(id, dto);
        return ResponseEntity.ok(productMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Remove a product from the catalog. **ADMIN role required.**")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products", description = "Search and filter products by various criteria")
    public ResponseEntity<List<ProductResponseDto>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean hasDiscount,
            @RequestParam(required = false) String sort) {
        Specification<Product> spec = ProductSpecification.filterProducts(
                categoryId, minPrice, maxPrice, hasDiscount, sort);

        List<Product> products = productRepository.findAll(spec);
        List<ProductResponseDto> dtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/discount")
    @Operation(summary = "Add discount to product")
    public ResponseEntity<ProductResponseDto> addDiscount(
            @PathVariable Long id,
            @Valid @RequestBody ProductDiscountDto discountDto) {
        Product product = productService.addDiscount(id, discountDto);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @DeleteMapping("/{id}/discount")
    @Operation(summary = "Remove discount from product")
    public ResponseEntity<ProductResponseDto> removeDiscount(@PathVariable Long id) {
        Product product = productService.removeDiscount(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @GetMapping("/discounts")
    @Operation(summary = "Get all products with discount")
    public ResponseEntity<List<ProductResponseDto>> getProductsWithDiscount() {
        List<Product> products = productService.getProductsWithDiscount();
        List<ProductResponseDto> dtoList = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/discounts/category/{categoryId}")
    @Operation(summary = "Get products with discount by category")
    public ResponseEntity<List<ProductResponseDto>> getProductsWithDiscountByCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        List<Product> products = productService.getProductsWithDiscount(category);
        List<ProductResponseDto> dtoList = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/product-of-the-day")
    @Operation(summary = "Get product of the day - product with highest discount")
    public ResponseEntity<ProductResponseDto> getProductOfTheDay() {
        Product product = productService.getProductOfTheDay();
        return ResponseEntity.ok(productMapper.toDto(product));
    }
}
