package telran.project.gardenshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import telran.project.gardenshop.enums.DeliveryMethod;
import java.time.LocalDateTime;

@Data
public class OrderCreateRequestDto {

    @NotNull
    private DeliveryMethod deliveryMethod;

    @NotBlank
    private String address;

    @NotBlank
    private String contactName;

    @NotNull
    private LocalDateTime createdAt;
}