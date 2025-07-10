//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import telran.project.gardenshop.dto.UserRequestDto;
//import telran.project.gardenshop.dto.UserResponseDto;
//import telran.project.gardenshop.entity.User;
//import telran.project.gardenshop.enums.Role;
//import telran.project.gardenshop.exception.UserNotFoundException;
//import telran.project.gardenshop.repository.UserRepository;
//import telran.project.gardenshop.service.UserServiceImpl;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static org.mockito.BDDMockito.*;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import telran.project.gardenshop.service.UserServiceImpl;
//
//class UserServiceImplTest {
//
//    private UserRepository userRepository;
//    private PasswordEncoder passwordEncoder;
//    private UserServiceImpl userService;
//
//    @BeforeEach
//    void setUp() {
//        userRepository = mock(UserRepository.class);
//        passwordEncoder = mock(PasswordEncoder.class);
//        userService = new UserServiceImpl(userRepository, passwordEncoder);
//    }
//
//    @Test
//    void testCreateUser() {
//        UserRequestDto request = new UserRequestDto("alex@gmail.com", "password", "Alex Doe", "1234567890", null);
//        String encodedPassword = "encodedPassword";
//
//        given(passwordEncoder.encode("password")).willReturn(encodedPassword);
//
//        User savedUser = User.builder()
//                .id(1L)
//                .email("john@example.com")
//                .passwordHash(encodedPassword)
//                .fullName("John Doe")
//                .phoneNumber("1234567890")
//                .role(Role.USER)
//                .build();
//
//        given(userRepository.save(any(User.class))).willReturn(savedUser);
//
//        UserResponseDto response = userService.create(request);
//
//        assertNotNull(response);
//        assertEquals("john@example.com", response.getEmail());
//        assertEquals("John Doe", response.getFullName());
//        assertEquals(Role.USER, response.getRole());
//
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    void testGetAllUsers() {
//        User user1 = User.builder().id(1L).email("a@example.com").fullName("A").phoneNumber("1").role(Role.USER).build();
//        User user2 = User.builder().id(2L).email("b@example.com").fullName("B").phoneNumber("2").role(Role.ADMIN).build();
//
//        given(userRepository.findAll()).willReturn(Arrays.asList(user1, user2));
//
//        List<UserResponseDto> result = userService.getAll();
//
//        assertEquals(2, result.size());
//        assertEquals("a@example.com", result.get(0).getEmail());
//        assertEquals("b@example.com", result.get(1).getEmail());
//    }
//
//    @Test
//    void testGetById_Success() {
//        User user = User.builder()
//                .id(1L)
//                .email("john@example.com")
//                .fullName("John Doe")
//                .phoneNumber("1234567890")
//                .role(Role.USER)
//                .build();
//
//        given(userRepository.findById(1L)).willReturn(Optional.of(user));
//
//        UserResponseDto dto = userService.getById(1L);
//
//        assertEquals("john@example.com", dto.getEmail());
//        assertEquals("John Doe", dto.getFullName());
//    }
//
//    @Test
//    void testGetById_NotFound() {
//        given(userRepository.findById(1L)).willReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
//    }
//
//    @Test
//    void testDelete_Success() {
//        given(userRepository.existsById(1L)).willReturn(true);
//
//        userService.delete(1L);
//
//        verify(userRepository).deleteById(1L);
//    }
//
//    @Test
//    void testDelete_NotFound() {
//        given(userRepository.existsById(1L)).willReturn(false);
//
//        assertThrows(UserNotFoundException.class, () -> userService.delete(1L));
//    }
//}