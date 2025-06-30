package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", source = "category.category") // или category.name — в зависимости от поля
    ProductResponseDto toDto(Product product);

    // Для создания сущности из DTO — не мапим category, тк у нас только имя категории в DTO
    Product toEntity(ProductRequestDto dto);

}