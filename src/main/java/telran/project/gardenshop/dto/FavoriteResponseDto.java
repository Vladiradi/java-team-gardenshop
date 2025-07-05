package telran.project.gardenshop.dto;

import lombok.Data;

@Data
public class FavoriteResponseDto {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private Double price;
    private String imageUrl;
}