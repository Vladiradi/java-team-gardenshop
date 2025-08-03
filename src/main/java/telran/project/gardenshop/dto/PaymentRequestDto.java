package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telran.project.gardenshop.enums.PaymentMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    private Long orderId;

    private PaymentMethod method;

}
