//package telran.project.gardenshop.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//        import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import telran.project.gardenshop.dto.UserRequestDto;
//import telran.project.gardenshop.dto.UserResponseDto;
//import telran.project.gardenshop.service.UserService;
//import telran.project.gardenshop.enums.Role;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//public class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    @Test
//    void createUser_ReturnsCreatedUser() throws Exception {
//        UserRequestDto requestDto = new UserRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setFullName("Test User");
//        requestDto.setPassword("password123");
//        requestDto.setPhoneNumber("1234567890");
//
//        UserResponseDto responseDto = new UserResponseDto(1L, "Test User", "test@example.com", "1234567890", Role.USER);
//
//        when(userService.create(any(UserRequestDto.class))).thenReturn(responseDto);
//
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.email").value("test@example.com"))
//                .andExpect(jsonPath("$.fullName").value("Test User"))
//                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
//                .andExpect(jsonPath("$.role").value("USER"));
//
//        verify(userService, times(1)).create(any(UserRequestDto.class));
//    }
//
//    @Test
//    void getAll_ReturnsListOfUsers() throws Exception {
//        UserResponseDto user1 = new UserResponseDto(1L, "User One", "user1@example.com", "111111", Role.USER);
//        UserResponseDto user2 = new UserResponseDto(2L, "User Two", "user2@example.com", "222222", Role.ADMIN);
//
//        when(userService.getAll()).thenReturn(List.of(user1, user2));
//
//        mockMvc.perform(get("/api/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
//                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
//
//        verify(userService, times(1)).getAll();
//    }
//
//    @Test
//    void getById_ReturnsUser() throws Exception {
//        UserResponseDto responseDto = new UserResponseDto(1L, "User One", "user1@example.com", "111111", Role.USER);
//
//        when(userService.getById(1L)).thenReturn(responseDto);
//
//        mockMvc.perform(get("/api/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.email").value("user1@example.com"));
//
//        verify(userService, times(1)).getById(1L);
//    }
//
//    @Test
//    void deleteUser_DeletesUser() throws Exception {
//        doNothing().when(userService).delete(1L);
//
//        mockMvc.perform(delete("/api/users/1"))
//                .andExpect(status().isOk());
//
//        verify(userService, times(1)).delete(1L);
//    }
//}