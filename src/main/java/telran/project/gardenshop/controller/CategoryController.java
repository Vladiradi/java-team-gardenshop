package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @Operation(summary = "Создать категорию")
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto dto) {
        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(categoryMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию по ID")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @GetMapping
    @Operation(summary = "Получить все категории")
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(
                categories.stream()
                        .map(categoryMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить категорию")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryRequestDto dto) {
        Category updated = categoryMapper.toEntity(dto);
        Category saved = categoryService.updateCategory(id, updated);
        return ResponseEntity.ok(categoryMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}