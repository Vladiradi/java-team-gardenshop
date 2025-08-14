package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;


@Mapper(componentModel = "spring", uses = {FavoriteMapper.class})
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    @Mapping(source = "role", target = "role")
    @Mapping(source = "favorites", target = "favorites")
    UserResponseDto toDto(User user);

    void updateUserFromDto(UserEditDto dto, @MappingTarget User user);
}
