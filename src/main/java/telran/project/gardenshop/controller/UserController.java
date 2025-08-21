package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Register a new user in the system")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = userMapper.toEntity(dto);
        User saved = userService.createUser(user);
        return ResponseEntity.status(201).body(userMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by their unique identifier")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toDto(userService.getUserById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(
                userService.getAllUsers().stream()
                        .map(userMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update existing user information")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserEditDto dto) {
        User updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Remove a user from the system")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
