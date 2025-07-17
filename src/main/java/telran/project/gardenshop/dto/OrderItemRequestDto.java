package telran.project.gardenshop.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequestDto {
    private Long orderId;
    private Long productId;
    private Integer quantity;
}
