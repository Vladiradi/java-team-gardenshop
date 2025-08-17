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

    @Mapping(source = "category", target = "categoryName", qualifiedByName = "extractCategoryName")
    @Mapping(target = "hasDiscount", expression = "java(product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(product.getPrice()) < 0)")
    @Mapping(target = "currentPrice", expression = "java(product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(product.getPrice()) < 0 ? product.getDiscountPrice().doubleValue() : product.getPrice().doubleValue())")
    @Mapping(target = "discountPercentage", expression = "java(calculateDiscountPercentage(product))")
    @Mapping(target = "discountAmount", expression = "java(calculateDiscountAmount(product))")
    ProductResponseDto toDto(Product product);

    @Mapping(source = "categoryId", target = "category.id")
    Product toEntity(ProductRequestDto dto);

    @Named("extractCategoryName")
    static String extractCategoryName(Category category) {
        return category != null ? category.getName() : null;
    }

    static double calculateDiscountPercentage(Product product) {
        if (product.getDiscountPrice() == null || product.getDiscountPrice().compareTo(product.getPrice()) >= 0) {
            return 0.0;
        }
        double discountAmount = product.getPrice().subtract(product.getDiscountPrice()).doubleValue();
        return (discountAmount / product.getPrice().doubleValue()) * 100;
    }

    static double calculateDiscountAmount(Product product) {
        if (product.getDiscountPrice() == null || product.getDiscountPrice().compareTo(product.getPrice()) >= 0) {
            return 0.0;
        }
        return product.getPrice().subtract(product.getDiscountPrice()).doubleValue();
    }
}