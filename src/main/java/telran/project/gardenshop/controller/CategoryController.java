package telran.project.gardenshop.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponceDto create(@Valid @RequestBody CategoryRequestDto dto) {
        return categoryService.create(dto);
    }

    @GetMapping
    public List<CategoryResponceDto> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryResponceDto getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

}
