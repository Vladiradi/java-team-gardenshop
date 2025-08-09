package telran.project.gardenshop.dto;

import lombok.Data;
import telran.project.gardenshop.enums.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderShortResponseDto {

    private Long id;

    private OrderStatus status;

    private LocalDateTime createdAt;
}