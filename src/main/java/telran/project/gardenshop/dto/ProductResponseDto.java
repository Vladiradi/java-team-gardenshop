package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.entity.Category;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private CategoryResponseDto category; // можно также вернуть categoryId или объект
}
