package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDto {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private Double price;

    private String imageUrl;
}
