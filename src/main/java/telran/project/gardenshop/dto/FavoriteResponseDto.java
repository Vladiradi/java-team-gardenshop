package telran.project.gardenshop.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponseDto {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private Double price;

    private String imageUrl;

}