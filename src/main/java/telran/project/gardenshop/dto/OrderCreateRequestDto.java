package telran.project.gardenshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.DeliveryMethod;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    @Builder.Default
    private List<OrderItemRequestDto> items = new ArrayList<>();

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Delivery method is required")
    private DeliveryMethod deliveryMethod;
}
