package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    User getCurrent();

    User getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    User updateUser(Long id, UserEditDto dto);

    void deleteUser(Long id);
}
