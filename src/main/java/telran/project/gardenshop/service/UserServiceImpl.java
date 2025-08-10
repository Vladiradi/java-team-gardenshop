package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.exception.UserWithEmailAlreadyExistsException;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public User create(User user) {
        emailCheck(user.getEmail());
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }


    @Override
    public User update(UserEditDto dto) {
        User user = getCurrent();
        if (!user.getEmail().equals(dto.getEmail())) {
            emailCheck(dto.getEmail());
        }
        userMapper.updateUserFromDto(dto, user);
        return userRepository.save(user);
    }

    @Override
    public void delete() {
        User user = getCurrent();
        userRepository.delete(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public User getCurrent() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void emailCheck(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserWithEmailAlreadyExistsException("User with email " + email + " already exists");
        }
    }
}
