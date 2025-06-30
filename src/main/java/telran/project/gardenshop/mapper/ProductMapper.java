package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "product", target = "categoryName", qualifiedByName = "extractCategoryName")
    ProductResponseDto toDto(Product product);

    @Named("extractCategoryName")
    static String extractCategoryName(Product product) {
        Category category = product.getCategory();
        return category != null ? category.getName() : null;
    }
}