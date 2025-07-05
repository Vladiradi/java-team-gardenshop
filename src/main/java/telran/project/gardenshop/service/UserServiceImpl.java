package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.common.fetcher.EntityFetcher;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityFetcher fetcher;

    @Override
    public User createUser(User user) {
        user.setRole(user.getRole() == null ? Role.USER : user.getRole());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return fetcher.fetchOrThrow(userRepository, id, "User");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updated) {
        User user = fetcher.fetchOrThrow(userRepository, id, "User");
        user.setFullName(updated.getFullName());
        user.setPhoneNumber(updated.getPhoneNumber());
        user.setEmail(updated.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = fetcher.fetchOrThrow(userRepository, id, "User");
        userRepository.delete(user);
    }
}