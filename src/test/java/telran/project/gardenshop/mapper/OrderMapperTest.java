package telran.project.gardenshop.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapperImpl();
    private final OrderItemMapper orderItemMapper = new OrderItemMapperImpl();

    @Test
    void testToDto() {
        Order order = buildSampleOrder();

        OrderResponseDto dto = orderMapper.toDto(order);

        assertThat(dto).isNotNull();
        assertThat(dto.getAddress()).isEqualTo(order.getDeliveryAddress());
        assertThat(dto.getItems()).hasSize(1);
    }

    @Test
    void testToShortDto() {
        Order order = buildSampleOrder();

        OrderShortResponseDto dto = orderMapper.toShortDto(order);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(order.getId());
    }

    @Test
    void testToHistoryDto() {
        Order order = buildSampleOrder();
        String phone = "+123456789";

        OrderHistoryDto dto = orderMapper.toHistoryDto(order, phone);

        assertThat(dto).isNotNull();
        assertThat(dto.getRecipientPhone()).isEqualTo(phone);
        assertThat(dto.getTotalPrice()).isEqualTo(19.98); // 9.99 * 2
        assertThat(dto.getCreatedAt()).isEqualTo(order.getCreatedAt().format(OrderMapper.DATE_FORMATTER));
    }

    @Test
    void testToHistoryList() {
        Order order1 = buildSampleOrder();
        Order order2 = buildSampleOrder();
        String phone = "+987654321";

        List<OrderHistoryDto> list = orderMapper.toHistoryList(List.of(order1, order2), phone);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getRecipientPhone()).isEqualTo(phone);
    }

    private Order buildSampleOrder() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Rose");
        product.setImageUrl("rose.jpg");

        OrderItem item = new OrderItem();
        item.setId(10L);
        item.setProduct(product);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(9.99));

        Order order = new Order();
        order.setId(100L);
        order.setDeliveryAddress("123 Garden St");
        order.setContactName("John Doe");
        order.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 30));
        order.setStatus(OrderStatus.NEW);
        order.setItems(List.of(item));

        return order;
    }
}
