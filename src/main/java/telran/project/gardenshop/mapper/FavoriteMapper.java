package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    default FavoriteResponseDto toDto(Favorite favorite) {
        if (favorite == null || favorite.getProduct() == null) {
            return null;
        }
        Product product = favorite.getProduct();
        return FavoriteResponseDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price((int) product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }

    List<FavoriteResponseDto> toDtoList(List<Favorite> favorites);

    // Метод для создания Favorite из готовых сущностей User и Product
    default Favorite toEntity(User user, Product product) {
        if (user == null || product == null) {
            return null;
        }
        return Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
