package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    @Mapping(source = "role", target = "role")
    @Mapping(source = "favorites", target = "favorites", qualifiedByName = "mapFavorites")
    UserResponseDto toDto(User user);

    @Named("mapFavorites")
    static List<FavoriteResponseDto> mapFavorites(List<Favorite> favorites) {
        if (favorites == null || favorites.isEmpty()) return Collections.emptyList();

        return favorites.stream()
                .map(fav -> FavoriteResponseDto.builder()
                        .id(fav.getId())
                        .userId(fav.getUser() != null ? fav.getUser().getId() : null)
                        .productId(fav.getProduct() != null ? fav.getProduct().getId() : null)
                        .productName(fav.getProduct() != null ? fav.getProduct().getName() : null)
                        .price(fav.getProduct() != null ? fav.getProduct().getPrice() : null)
                        .imageUrl(fav.getProduct() != null ? fav.getProduct().getImageUrl() : null)
                        .build())
                .collect(Collectors.toList());
    }
}