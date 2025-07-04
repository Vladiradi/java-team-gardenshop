package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;

import java.util.List;

public interface UserService {
    User create(User user);
    List<User> getAll();
    User getById(Long id);
    void delete(Long id);
}