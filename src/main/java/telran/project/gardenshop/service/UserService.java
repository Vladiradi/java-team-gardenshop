package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;

import java.security.Principal;
import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User updated);
    void deleteUser(Long id);
    void editUser(Long id, UserEditDto dto, Principal principal);
}