package telran.project.gardenshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import telran.project.gardenshop.dto.OrderHistoryDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.entity.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    // Основные маппинги (как были)
    @Mapping(source = "deliveryAddress", target = "address")
    @Mapping(source = "items", target = "items")
    OrderResponseDto toDto(Order order);

    OrderShortResponseDto toShortDto(Order order);

    default OrderHistoryDto toHistoryDto(Order order, String recipientPhone) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponseDto> products = (order.getItems() == null || order.getItems().isEmpty())
                ? List.of()
                : order.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> OrderItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                        .productImageUrl(item.getProduct() != null ? item.getProduct().getImageUrl() : null)
                        .quantity(item.getQuantity())
                        .price(item.getPrice() != null ? item.getPrice().doubleValue() : null)
                        .build())
                .toList();

        BigDecimal total = (order.getItems() == null || order.getItems().isEmpty())
                ? BigDecimal.ZERO
                : order.getItems().stream()
                .filter(Objects::nonNull)
                .map(i -> {
                    BigDecimal price = i.getPrice() != null ? i.getPrice() : BigDecimal.ZERO;
                    int qty = i.getQuantity();
                    return price.multiply(BigDecimal.valueOf(qty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderHistoryDto.builder()
                .orderId(order.getId())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalPrice(total.doubleValue())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null)
                .products(products)
                .deliveryAddress(order.getDeliveryAddress())
                .recipientName(order.getContactName())
                .recipientPhone(recipientPhone)
                .build();
    }


    default List<OrderHistoryDto> toHistoryList(List<Order> orders, String recipientPhone) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }
        return orders.stream()
                .filter(Objects::nonNull)
                .map(o -> toHistoryDto(o, recipientPhone))
                .toList();
    }
}