package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();
  
    User updateUser(Long id, UserEditDto dto);
  
    void deleteUser(Long id);
}