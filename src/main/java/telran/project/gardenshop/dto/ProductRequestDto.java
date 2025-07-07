package telran.project.gardenshop.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product name must not be blank")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @NotBlank(message = "Image URL must not be blank")
    @Size(max = 1000, message = "Image URL must be at most 1000 characters")
    private String imageUrl;

    @NotNull(message = "Category ID must not be null")
    @Min(value = 1, message = "Category ID must be a positive number")
    private Long categoryId;
}