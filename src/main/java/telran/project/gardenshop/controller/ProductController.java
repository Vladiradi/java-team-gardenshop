package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.ProductMapper;
import telran.project.gardenshop.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    @Operation(summary = "Добавить новый товар")
    public ResponseEntity<ProductResponseDto> create(@RequestBody ProductRequestDto dto) {
        Product entity = productMapper.toEntity(dto);
        Product saved = productService.createProduct(entity);
        return ResponseEntity.status(201).body(productMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @GetMapping
    @Operation(summary = "Получить все товары")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDto> dtoList = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Long id,
                                                     @RequestBody ProductRequestDto dto) {
        Product updated = productMapper.toEntity(dto);
        Product saved = productService.updateProduct(id, updated);
        return ResponseEntity.ok(productMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}