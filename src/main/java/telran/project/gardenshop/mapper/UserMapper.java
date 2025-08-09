package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    @Mapping(source = "role", target = "role")
    @Mapping(source = "favorites", target = "favorites", qualifiedByName = "mapFavorites")
    UserResponseDto toDto(User user);

    void updateUserFromDto(UserEditDto dto, @MappingTarget User user);

    @Named("mapFavorites")
    static List<FavoriteResponseDto> mapFavorites(List<Favorite> favorites) {
        if (favorites == null || favorites.isEmpty()) {
            return List.of();
        }

        return favorites.stream()
                .filter(Objects::nonNull)
                .map(fav -> FavoriteResponseDto.builder()
                        .id(fav.getId())
                        .userId(fav.getUser() != null ? fav.getUser().getId() : null)
                        .productId(fav.getProduct() != null ? fav.getProduct().getId() : null)
                        .productName(fav.getProduct() != null ? fav.getProduct().getName() : null)
                        .price(fav.getProduct() != null && fav.getProduct().getPrice() != null
                                ? fav.getProduct().getPrice().doubleValue()
                                : null)
                        .imageUrl(fav.getProduct() != null ? fav.getProduct().getImageUrl() : null)
                        .build())
                .collect(Collectors.toList());
    }
}