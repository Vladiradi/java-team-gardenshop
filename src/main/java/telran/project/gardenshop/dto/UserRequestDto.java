package telran.project.gardenshop.dto;

import lombok.Data;
import telran.project.gardenshop.enums.Role;

@Data
public class UserRequestDto {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private Role role;
}
