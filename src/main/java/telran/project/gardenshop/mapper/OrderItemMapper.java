package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderItemResponseDto toDto(OrderItem orderItem);

    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "productId", target = "product.id")
    OrderItem toEntity(OrderItemRequestDto dto);

//    // если надо обновлять сущность из DTO
//    @Mapping(source = "orderId", target = "order.id")
//    @Mapping(source = "productId", target = "product.id")
//    void updateEntityFromDto(OrderItemRequestDto dto, @MappingTarget OrderItem orderItem);
}
