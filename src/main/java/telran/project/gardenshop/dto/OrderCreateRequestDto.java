package telran.project.gardenshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import telran.project.gardenshop.enums.DeliveryMethod;
import java.util.List;

@Data
public class OrderCreateRequestDto {

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<OrderItemRequestDto> items;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Delivery method is required")
    private DeliveryMethod deliveryMethod;
}
