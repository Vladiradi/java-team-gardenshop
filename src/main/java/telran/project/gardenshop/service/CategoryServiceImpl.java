package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.repository.CategoryRepository;
import telran.project.gardenshop.exception.CategoryNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryEditDto dto) {
        Category category = getCategoryById(id);
        categoryMapper.updateEntityFromDto(dto, category);
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.delete(getCategoryById(id));
    }
}