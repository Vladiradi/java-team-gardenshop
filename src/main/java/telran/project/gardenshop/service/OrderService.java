package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Order getOrderById(Long orderId);

    List<Order> getOrdersForCurrent();

    List<Order> getOrdersByUserId(Long userId);

    List<Order> getActiveOrders();

    List<Order> findAll();

    BigDecimal getTotalAmount(Long orderId);

    Order createOrderForCurrentUser(OrderCreateRequestDto dto);

    void deleteOrder(Long orderId);

    Order updateStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);
}
