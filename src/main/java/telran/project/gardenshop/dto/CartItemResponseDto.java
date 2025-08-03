package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    private Long id;

    private Long cartId;

    private Long productId;

    private Integer quantity;

    private Double price;

    private String productName;

}