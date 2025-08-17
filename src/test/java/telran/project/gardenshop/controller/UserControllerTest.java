package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserRequestDto userRequestDto;
    private UserEditDto userEditDto;
    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        userRequestDto = UserRequestDto.builder()
                .email("test@example.com")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .password("pass123")
                .build();

        userEditDto = UserEditDto.builder()
                .email("test@example.com")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .password("pass123")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(userMapper.toEntity(any())).thenReturn(user);
        when(userService.createUser(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userResponseDto);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userResponseDto);

        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(any())).thenReturn(userResponseDto);

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        when(userService.updateUser(1L, userEditDto)).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userResponseDto);

        mockMvc.perform(put("/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEditDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}
