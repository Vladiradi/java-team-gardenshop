package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.exception.UserWithEmailAlreadyExistsException;
import telran.project.gardenshop.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        emailCheck(user.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updated) {
        User user = getUserById(id);

        if (!user.getEmail().equals(updated.getEmail())) {
            emailCheck(updated.getEmail());
        }

        user.setFullName(updated.getFullName());
        user.setPhoneNumber(updated.getPhoneNumber());
        user.setEmail(updated.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void editUser(Long id, UserEditDto dto, Principal principal) {
        User user = getUserById(id);

//        // Только владелец может обновить себя
//        if (!user.getEmail().equals(principal.getName())) {
//            throw new AccessDeniedException("You can only edit your own profile");
//        }

        // Обновление email
        String newEmail = dto.getEmail();
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            emailCheck(newEmail);
            user.setEmail(newEmail);
        }

        // Обновление ФИО
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }

        // Обновление пароля (только если указан)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // Utility method for email uniqueness check
    private void emailCheck(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserWithEmailAlreadyExistsException("User with email " + email + " already exists");
        }
    }
}