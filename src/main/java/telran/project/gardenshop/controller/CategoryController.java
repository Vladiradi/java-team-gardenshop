package telran.project.gardenshop.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.service.CategoryService;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponseDto create(@Valid @RequestBody CategoryRequestDto dto) {
        return categoryService.create(dto);
    }

    @GetMapping
    public List<CategoryResponseDto> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @PutMapping("/{id}")
    public CategoryResponseDto update(@PathVariable Long id, @RequestBody CategoryEditDto dto) {
        return categoryService.update(id, dto);
    }

}
