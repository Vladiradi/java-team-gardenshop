package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserRequestDto dto);
    List<UserResponseDto> getAll();
    UserResponseDto getById(Long id);
    void delete(Long id);
}