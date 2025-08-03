package telran.project.gardenshop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryDto {

    private Long orderId;

    private String status;

    private Double totalPrice;

    private String createdAt;

    private List<OrderItemResponseDto> products;

    private String deliveryAddress;

    private String recipientName;

    private String recipientPhone;

}