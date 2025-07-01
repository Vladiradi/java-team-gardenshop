package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    FavoriteResponseDto toDto(Favorite favorite);

    // Для создания Favorite из User и Product MapStruct не умеет маппить сразу из двух источников,
    // поэтому используем default метод, чтобы сохранить логику централизованной конвертации
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


