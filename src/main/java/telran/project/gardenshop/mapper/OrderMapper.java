package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.Order;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(source = "deliveryAdress", target = "address")
    @Mapping(source = "items", target = "items")
    OrderResponseDto toDto(Order order);

    OrderShortResponseDto toShortDto(Order order);
}