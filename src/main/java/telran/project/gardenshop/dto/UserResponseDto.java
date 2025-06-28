package telran.project.gardenshop.dto;

import telran.project.gardenshop.enums.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
}
