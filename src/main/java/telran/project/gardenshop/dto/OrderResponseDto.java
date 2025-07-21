package telran.project.gardenshop.dto;

import lombok.Data;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {

    private Long id;

    private OrderStatus status;

    private DeliveryMethod deliveryMethod;

    private String address;

    private String contactName;

    private LocalDateTime createdAt;

    private List<OrderItemDto> items;
}
