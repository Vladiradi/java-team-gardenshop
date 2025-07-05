package telran.project.gardenshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequestDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long productId;
}