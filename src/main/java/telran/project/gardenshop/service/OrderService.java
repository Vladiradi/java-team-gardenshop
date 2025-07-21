package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Order;
import java.util.List;

public interface OrderService {

    Order getOrderById(Long orderId);

    List<Order> getOrdersByUserId(Long userId);

    List<Order> getActiveOrders();

    BigDecimal getTotalAmount(Long orderId);

    Order createOrder(Long userId, OrderCreateRequestDto dto);

    Order updateStatus(Long orderId, OrderStatus status);

    Order addItem(Long orderId, Long productId, Integer quantity);

    Order updateItem(Long orderItemId, Integer quantity);

    Order removeItem(Long orderItemId);

    void cancelOrder(Long orderId);
}