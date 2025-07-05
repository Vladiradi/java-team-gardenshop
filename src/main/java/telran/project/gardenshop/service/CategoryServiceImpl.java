package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.common.fetcher.EntityFetcher;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityFetcher fetcher;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = fetcher.fetchOrThrow(categoryRepository, id, "Category");
        category.setName(updatedCategory.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return fetcher.fetchOrThrow(categoryRepository, id, "Category");
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = fetcher.fetchOrThrow(categoryRepository, id, "Category");
        categoryRepository.delete(category);
    }
}