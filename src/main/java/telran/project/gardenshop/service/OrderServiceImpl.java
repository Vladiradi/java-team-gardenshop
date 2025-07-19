package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderRepository;
import telran.project.gardenshop.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public Order createOrder(Order order) {
        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW); // Статус по умолчанию
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existing = getOrderById(id);
        existing.setDeliveryMethod(updatedOrder.getDeliveryMethod());
        existing.setDeliveryAddress(updatedOrder.getDeliveryAddress());
        existing.setContactName(updatedOrder.getContactName());
        existing.setStatus(updatedOrder.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(existing);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

}
