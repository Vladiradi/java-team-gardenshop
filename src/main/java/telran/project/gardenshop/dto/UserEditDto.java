package telran.project.gardenshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDto {
    @NotBlank(message = "Full name must not be blank")
    private String fullName;
    // NotBlank — поле обязательно для заполнения (не null и не пустая строка)

    @Email(message = "Invalid email format")
    private String email;
    // Email — проверка, что email соответствует корректному формату

    private String password;
    // Password может быть пустым — если пользователь не хочет его менять
}
