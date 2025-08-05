package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.service.UserService;
import telran.project.gardenshop.service.security.JwtFilter;
import telran.project.gardenshop.service.security.JwtService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDto userRequestDto;
    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
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

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(any())).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}