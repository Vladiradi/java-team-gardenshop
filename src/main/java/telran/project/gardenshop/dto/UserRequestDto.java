package telran.project.gardenshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import telran.project.gardenshop.enums.Role;

@Data
public class UserRequestDto {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;

    @NotBlank(message = "Полное имя обязательно")
    @Size(min = 2, max = 100, message = "Длина полного имени от 2 до 100 символов")
    private String fullName;

    @NotBlank(message = "Номер телефона обязателен")
    @Size(min = 10, max = 15, message = "Длина номера телефона от 10 до 15 символов")
    private String phoneNumber;

    // Предполагаю, что Role — это enum, можно проверить на null если нужно
    @NotNull(message = "Роль пользователя обязана быть указана")
    private Role role;
}
