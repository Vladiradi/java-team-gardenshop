package telran.project.gardenshop.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Long categoryId;
}
