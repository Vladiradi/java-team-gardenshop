package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import java.util.List;

public interface CategoryService {
    CategoryResponseDto create(CategoryRequestDto dto);
    List<CategoryResponseDto> getAll();
    CategoryResponseDto getById(Long id);
    void delete(Long id);
}
