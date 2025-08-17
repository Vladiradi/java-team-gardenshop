package telran.project.gardenshop.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private String imageUrl;

    private String categoryName;

    private boolean hasDiscount;
    
    private Double currentPrice;
    
    private Double discountPercentage;
    
    private Double discountAmount;
}
