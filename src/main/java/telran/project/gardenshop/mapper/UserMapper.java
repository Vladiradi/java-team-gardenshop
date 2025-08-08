package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.*;
import java.util.List;
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
        if (favorites == null) return null;

        return favorites.stream()
                .map(fav -> FavoriteResponseDto.builder()
                        .id(fav.getId())
                        .userId(fav.getUser().getId())
                        .productId(fav.getProduct().getId())
                        .productName(fav.getProduct().getName())
                        .price(fav.getProduct().getPrice().doubleValue())
                        .imageUrl(fav.getProduct().getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}