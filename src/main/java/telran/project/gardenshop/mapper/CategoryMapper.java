package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequestDto dto);
    CategoryResponseDto toDto(Category entity);
    @Mapping(source = "name", target = "category")
    void updateCategoryFromEditDto(CategoryEditDto dto, @MappingTarget Category entity);
}