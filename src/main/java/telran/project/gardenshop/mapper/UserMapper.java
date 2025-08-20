package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.*;


@Mapper(componentModel = "spring", uses = { FavoriteMapper.class })
public interface UserMapper {

    @Mapping(target = "role", constant = "USER")
    User toEntity(UserRequestDto dto);


    @Mapping(source = "role", target = "role")
    @Mapping(source = "favorites", target = "favorites")
    UserResponseDto toDto(User user);

    void updateUserFromDto(UserEditDto dto, @MappingTarget User user);
}
