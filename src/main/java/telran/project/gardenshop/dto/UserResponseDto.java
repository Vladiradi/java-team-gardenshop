package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.Role;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
}
