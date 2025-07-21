package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDto toDto(Order order);

    OrderShortResponseDto toShortDto(Order order);
}