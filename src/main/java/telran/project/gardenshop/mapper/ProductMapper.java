package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Product → DTO
    @Mapping(source = "category", target = "categoryName", qualifiedByName = "extractCategoryName")
    ProductResponseDto toDto(Product product);

    // DTO → Product (create/update)
    @Mapping(source = "categoryId", target = "category.id")
    Product toEntity(ProductRequestDto dto);

    @Named("extractCategoryName")
    static String extractCategoryName(Category category) {
        return category != null ? category.getName() : null;
    }
}