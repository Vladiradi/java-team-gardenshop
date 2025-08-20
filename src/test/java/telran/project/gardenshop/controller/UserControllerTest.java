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
import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest extends AbstractTest {

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

        @BeforeEach
        protected void setUp() {
                super.setUp();
                mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        void create_shouldReturnCreatedUser() throws Exception {
                when(passwordEncoder.encode(any())).thenReturn("encodedPass");
                when(userMapper.toEntity(any())).thenReturn(user1);
                when(userService.createUser(any())).thenReturn(user1);
                when(userMapper.toDto(any())).thenReturn(userResponseDto1);

                mockMvc.perform(post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.email").value("alice.johnson@example.com"));
        }

        @Test
        void getById_shouldReturnUser() throws Exception {
                when(userService.getUserById(1L)).thenReturn(user1);
                when(userMapper.toDto(any())).thenReturn(userResponseDto1);

                mockMvc.perform(get("/v1/users/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("alice.johnson@example.com"));
        }

        @Test
        void getAll_shouldReturnList() throws Exception {
                when(userService.getAllUsers()).thenReturn(List.of(user1, user2));
                when(userMapper.toDto(user1)).thenReturn(userResponseDto1);
                when(userMapper.toDto(user2)).thenReturn(userResponseDto2);

                mockMvc.perform(get("/v1/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].email").value("alice.johnson@example.com"))
                                .andExpect(jsonPath("$[1].email").value("bob.smith@example.com"));
        }

//        @Test
//        void update_shouldReturnUpdatedUser() throws Exception {
//                UserEditDto editDto = UserEditDto.builder()
//                                .email("new@example.com")
//                                .fullName("New Name")
//                                .phoneNumber("+98765443210")
//                                .password("newpass")
//                                .build();
//
//                when(userService.updateUser(eq(1L), any(UserEditDto.class))).thenReturn(user1);
//                when(userMapper.toDto(any())).thenReturn(userResponseDto1);
//
//                mockMvc.perform(put("/v1/users/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(editDto)))
//                                .andExpect(status().isOk())
//                                .andDo(print())
//                                .andExpect(jsonPath("$.email").value("alice.johnson@example.com"));
//        }
//userService.updateUser(1L, editDto)).thenReturn(user1
        @Test
        void delete_shouldReturnNoContent() throws Exception {
                mockMvc.perform(delete("/v1/users/1"))
                                .andExpect(status().isNoContent());
        }
}
