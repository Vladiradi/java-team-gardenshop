package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteResponseDto {
    private Long id;
    private UserResponseDto user;
    private ProductResponseDto product;
    private LocalDateTime createdAt;
}
