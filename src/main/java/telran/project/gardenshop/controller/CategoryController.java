package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categories", description = "Category management operations")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Add a new product category to the system")
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto dto) {
        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve category information by unique identifier")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a list of all available product categories")
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(
                categories.stream()
                        .map(categoryMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Modify existing category information")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody CategoryEditDto dto) {
        Category saved = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(categoryMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Remove a category from the system")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
