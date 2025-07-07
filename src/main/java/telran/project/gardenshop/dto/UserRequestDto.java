package telran.project.gardenshop.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class UserRequestDto {

    @NotNull(message = "Full name must not be null")
    @NotBlank(message = "Full name must not be blank")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Email must not be null")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Phone number must not be null")
    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Phone number must be valid (10–15 digits, optional +)")
    private String phoneNumber;

    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Role must not be null (USER or ADMIN)")
    @NotBlank(message = "Role must not be blank (USER or ADMIN)")
    private String role; // USER or ADMIN
}