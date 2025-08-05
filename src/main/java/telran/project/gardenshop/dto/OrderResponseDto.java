package telran.project.gardenshop.dto;

import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;

    private OrderStatus status;

    private DeliveryMethod deliveryMethod;

    private String address;

    private String contactName;

    private LocalDateTime createdAt;

    private List<OrderItemResponseDto> items;
}
