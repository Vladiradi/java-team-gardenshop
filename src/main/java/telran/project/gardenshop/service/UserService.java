package telran.project.gardenshop.service;

import dto.UserRequestDto;
import dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserRequestDto dto);
    List<UserResponseDto> getAll();
    UserResponseDto getById(Long id);
    void delete(Long id);
}