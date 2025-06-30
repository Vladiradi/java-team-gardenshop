package telran.project.gardenshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    @NotBlank(message = "Product name must not be empty")
    private String name;
    private String description;
    @Positive(message = "Price must be positive")
    private double price;
    private String imageUrl;
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
