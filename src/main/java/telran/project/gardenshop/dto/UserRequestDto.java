package telran.project.gardenshop.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
    @Pattern(
            regexp = "^\\+?\\d{10,15}$",
            message = "Phone number must contain 10 to 15 digits and may start with '+'"
    )
    private String phoneNumber;

    private String password;

}
