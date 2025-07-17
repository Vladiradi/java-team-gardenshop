package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.OrderItem;

import java.util.List;

public interface OrderItemService {

    OrderItem createOrderItem(OrderItem orderItem);

    OrderItem getOrderItemById(Long id);

    void deleteOrderItem(Long id);

    List<OrderItem> getAllByOrderId(Long orderId);
}
