package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    // из DTO в Entity (без passwordHash)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserRequestDto dto);
}
