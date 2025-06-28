package telran.project.gardenshop.dto;

import lombok.Data;
import enums.Role;

public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
}
