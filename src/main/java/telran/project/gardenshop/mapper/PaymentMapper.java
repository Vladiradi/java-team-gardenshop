package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.PaymentResponseDto;
import telran.project.gardenshop.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "order.id")
    PaymentResponseDto toDto(Payment payment);
}
