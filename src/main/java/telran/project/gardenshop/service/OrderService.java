package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Order;
import java.util.List;

public interface OrderService {

    Order createOrder(Order order);

    Order getOrderById(Long id);

    List<Order> getAllOrders();

    List<Order> getOrdersByUserId(Long userId);

    Order updateOrder(Long id, Order updatedOrder);

    void deleteOrder(Long id);
}