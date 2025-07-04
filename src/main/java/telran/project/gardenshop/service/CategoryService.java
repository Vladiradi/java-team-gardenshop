package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;

import java.util.List;

public interface CategoryService {
    Category create(CategoryRequestDto dto);
    List<Category> getAll();
    Category getById(Long id);
    void delete(Long id);
}
