package telran.project.gardenshop.mapper;
package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequestDto dto);
    CategoryResponseDto toDto(Category entity);
}