package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.entity.Category;
import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);

    Category updateCategory(Long id, CategoryEditDto dto);

    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    void deleteCategory(Long id);
}