package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {
    private Long productId;
    private Integer quantity;
    private Double price;
}