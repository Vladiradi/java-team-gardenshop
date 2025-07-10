package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testUpdateUser_Success() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@email.com");

        User updatedUser = new User();
        updatedUser.setFullName("New Name");
        updatedUser.setPhoneNumber("1234567890");
        updatedUser.setEmail("new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(userId, updatedUser);

        assertEquals("New Name", result.getFullName());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("new@email.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateUser_NotFound() {
        Long userId = 1L;

        User updatedUser = new User();
        updatedUser.setFullName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updatedUser));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }
}