package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    @PostMapping
    @Operation(summary = "Add new product")
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto dto) {
        Product entity = productMapper.toEntity(dto);
        Product saved = productService.createProduct(entity);
        return ResponseEntity.status(201).body(productMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDto> dtoList = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody ProductRequestDto dto) {
        Product entity = productMapper.toEntity(dto);
        Product updated = productService.updateProduct(id, entity);
        return ResponseEntity.ok(productMapper.toDto(updated));
    }

    @PutMapping("/{id}/edit")
    @Operation(summary = "Edit product (title, description, price)")
    public ResponseEntity<ProductResponseDto> editProduct(
            @PathVariable Long id,
            @RequestBody ProductEditDto dto) {

        Product updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(productMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
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

    // Эндпоинты для управления скидками
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
}
