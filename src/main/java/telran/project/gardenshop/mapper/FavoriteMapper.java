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

    FavoriteResponseDto toDto(Favorite favorite);

    List<FavoriteResponseDto> toDtoList(List<Favorite> favorites);

    // В этом методе мы НЕ сможем напрямую замапить userId → User и productId → Product,
    // поэтому лучше использовать ручной mapping:
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
