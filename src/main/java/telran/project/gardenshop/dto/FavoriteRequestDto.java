package telran.project.gardenshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FavoriteRequestDto {

    @NotNull(message = "User ID must not be null")
    @Min(value = 1, message = "User ID must be a positive number")
    private Long userId;

    @NotNull(message = "Product ID must not be null")
    @Min(value = 1, message = "Product ID must be a positive number")
    private Long productId;

}