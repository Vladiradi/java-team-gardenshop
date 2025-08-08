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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    //private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public User createUser(User user) {
        emailCheck(user.getEmail());
        user.setRole(Role.USER);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updated) {
        return null;
    }

    @Override
    public User updateUser(Long id, UserEditDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getEmail().equals(dto.getEmail())) {
            emailCheck(dto.getEmail());
        }

        userMapper.updateUserFromDto(dto, user);
        // user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void emailCheck(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserWithEmailAlreadyExistsException("User with email " + email + " already exists");
        }
    }
}