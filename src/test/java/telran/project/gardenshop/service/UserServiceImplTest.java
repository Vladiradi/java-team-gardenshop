package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.exception.UserWithEmailAlreadyExistsException;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserEditDto testEditDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .fullName("Test User")
                .role(Role.USER)
                .phoneNumber("+1234567890")
                .build();

        testEditDto = UserEditDto.builder()
                .email("new@example.com")
                .fullName("New Name")
                .phoneNumber("+1234567890")
                .password("newpass")
                .build();
    }

    @Test
    void shouldCreateUser_whenEmailIsUnique() {
        User userToCreate = User.builder()
                .email("test@example.com")
                .password("password")
                .fullName("Test User")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(userToCreate);

        assertThat(created).isNotNull();
        assertThat(created.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(userToCreate);
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        User userToCreate = User.builder().email("test@example.com").build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(userToCreate))
                .isInstanceOf(UserWithEmailAlreadyExistsException.class);
    }

    @Test
    void shouldReturnUser_whenUserIdExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User found = userService.getUserById(1L);

        assertThat(found).isEqualTo(testUser);
    }

    @Test
    void shouldThrowException_whenUserIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReturnAllUsers_whenUsersExist() {
        List<User> users = List.of(
                User.builder().id(1L).email("a@example.com").build(),
                User.builder().id(2L).email("b@example.com").build());

        when(userRepository.findAll()).thenReturn(users);

        List<User> all = userService.getAllUsers();

        assertThat(all).hasSize(2);
    }

    @Test
    void shouldUpdateUser_whenValidInputProvided() {
        Long id = 1L;
        User existingUser = User.builder()
                .id(id)
                .email("old@example.com")
                .fullName("Old Name")
                .phoneNumber("+1234567890")
                .password("pass")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        doAnswer(inv -> {
            UserEditDto source = inv.getArgument(0);
            User target = inv.getArgument(1);
            target.setEmail(source.getEmail());
            target.setFullName(source.getFullName());
            target.setPhoneNumber(source.getPhoneNumber());
            return null;
        }).when(userMapper).updateUserFromDto(testEditDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updated = userService.updateUser(id, testEditDto);

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getFullName()).isEqualTo("New Name");
    }

    @Test
    void shouldDeleteUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    void shouldReturnUser_whenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertThat(result).isPresent().contains(testUser);
    }
}
