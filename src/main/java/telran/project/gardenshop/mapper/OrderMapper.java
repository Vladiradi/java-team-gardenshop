package telran.project.gardenshop.mapper;

import org.mapstruct.*;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.Order;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Mapping(source = "deliveryAddress", target = "address")
    @Mapping(source = "items", target = "items")
    OrderResponseDto toDto(Order order);

    OrderShortResponseDto toShortDto(Order order);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "status", expression = "java(order.getStatus() != null ? order.getStatus().name() : null)")
    @Mapping(target = "products", source = "order.items") // uses OrderItemMapper
    @Mapping(target = "totalPrice", ignore = true) // set in @AfterMapping
    @Mapping(target = "createdAt", ignore = true) // set in @AfterMapping
    @Mapping(target = "deliveryAddress", source = "order.deliveryAddress")
    @Mapping(target = "recipientName", source = "order.contactName")
    OrderHistoryDto toHistoryDto(Order order, @Context String recipientPhone);

    List<OrderHistoryDto> toHistoryList(List<Order> orders, @Context String recipientPhone);

    @AfterMapping
    default void fillHistoryExtras(Order order,
                                   @Context String recipientPhone,
                                   @MappingTarget OrderHistoryDto.OrderHistoryDtoBuilder builder) {
        builder.totalPrice(calculateTotalPrice(order));
        builder.createdAt(formatCreatedAt(order));
        builder.recipientPhone(recipientPhone);
    }

    private static double calculateTotalPrice(Order order) {
        if (order == null || order.getItems() == null) {
            return 0.0;
        }
        return order.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    BigDecimal price = Optional.ofNullable(item.getPrice()).orElse(BigDecimal.ZERO);
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    private static String formatCreatedAt(Order order) {
        return Optional.ofNullable(order)
                .map(Order::getCreatedAt)
                .map(DATE_FORMATTER::format)
                .orElse(null);
    }
}
