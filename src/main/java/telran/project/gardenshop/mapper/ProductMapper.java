package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDto dto);

    @Mapping(target = "category", source = "category") // в ProductResponseDto category — строка?
    ProductResponseDto toDto(Product product);

    // Явный метод преобразования Category в CategoryResponseDto
    default CategoryResponseDto map(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponseDto.builder()
                .id(category.getId())
                .category(category.getCategory())
                .build();
    }

    // Аналогично для обратного преобразования, если нужно
    default Category map(CategoryResponseDto dto) {
        if (dto == null) {
            return null;
        }
        return Category.builder()
                .id(dto.getId())
                .category(dto.getCategory())
                .build();
    }
}
