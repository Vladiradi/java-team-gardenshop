package telran.project.gardenshop.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {
    @NotBlank(message = "Название продукта обязательно")
    @Size(max = 255, message = "Название продукта не должно превышать 255 символов")
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Цена продукта обязательна")
    @PositiveOrZero(message = "Цена должна быть нулём или положительным числом")
    private Double price;

    @Size(max = 500, message = "URL изображения слишком длинный")
    private String imageUrl;

    @NotNull(message = "ID категории обязателен")
    private Long categoryId;
}
