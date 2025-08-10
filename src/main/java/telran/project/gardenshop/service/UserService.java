package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;
import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(Long id);

    User getByEmail(String email);

    User getCurrent();

    User create(User user);

    User update(User user);

    void delete();
}
