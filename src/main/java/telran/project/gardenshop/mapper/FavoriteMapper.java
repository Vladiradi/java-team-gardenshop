package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(target = "userId", expression = "java(favorite.getUser() != null ? favorite.getUser().getId() : null)")
    @Mapping(target = "productId", expression = "java(favorite.getProduct() != null ? favorite.getProduct().getId() : null)")
    @Mapping(target = "productName", expression = "java(favorite.getProduct() != null ? favorite.getProduct().getName() : null)")
    @Mapping(target = "price", expression = "java(favorite.getProduct() != null && favorite.getProduct().getPrice() != null ? favorite.getProduct().getPrice().doubleValue() : null)")
    @Mapping(target = "imageUrl", expression = "java(favorite.getProduct() != null ? favorite.getProduct().getImageUrl() : null)")
    FavoriteResponseDto toDto(Favorite favorite);

    default Favorite toEntity(User user, Product product) {
        return Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
