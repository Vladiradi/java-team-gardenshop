package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);
}