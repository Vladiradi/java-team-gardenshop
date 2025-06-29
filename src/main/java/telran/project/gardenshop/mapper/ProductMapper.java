package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDto dto);

    @Mapping(target = "category", source = "category.category") // в ProductResponseDto category — строка?
    ProductResponseDto toDto(Product product);

    // Преобразование из String в Category
    default Category map(String categoryName) {
        if (categoryName == null) return null;
        return Category.builder().category(categoryName).build();
    }

    // Преобразование из Category в String
    default String map(Category category) {
        return category != null ? category.getCategory() : null;
    }
}
