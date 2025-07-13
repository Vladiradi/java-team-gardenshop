package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, User updated);

    void deleteUser(Long id);

    User updateUser(Long id, UserEditDto dto);

    Optional<User> getUserByEmail(String email);

    User getCurrentUser();
}