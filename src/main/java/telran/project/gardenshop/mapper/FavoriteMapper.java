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
                .price((int) product.getPrice())  // приведение к int, если price — double
                .imageUrl(product.getImageUrl())
                .build();
    }

    List<FavoriteResponseDto> toDtoList(List<Favorite> favorites);

    default Favorite toEntity(FavoriteRequestDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getUserId());
        Product product = new Product();
        product.setId(dto.getProductId());
        return Favorite.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
