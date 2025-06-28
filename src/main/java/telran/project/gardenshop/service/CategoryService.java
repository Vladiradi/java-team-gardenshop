package telran.project.gardenshop.service;

import java.util.List;

public interface CategoryService {
    CategoryResponceDto create(CategoryRequestDto dto);
    List<CategoryResponceDto> getAll();
    CategoryResponceDto getById(Long id);
    void delete(Long id);
}
