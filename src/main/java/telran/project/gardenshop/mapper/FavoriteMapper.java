package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    @Mapping(source = "user.id", target = "userId")
    FavoriteResponseDto toDto(Favorite favorite);
}