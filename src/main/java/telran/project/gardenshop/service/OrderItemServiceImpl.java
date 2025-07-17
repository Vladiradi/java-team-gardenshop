package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.exception.OrderItemNotFoundException;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderItemRepository;
import telran.project.gardenshop.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService{

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        Order order = orderRepository.findById(orderItem.getOrder().getId())
                .orElseThrow(() -> new OrderNotFoundException(orderItem.getOrder().getId()));

        Product product = productService.getProductById(orderItem.getProduct().getId());

        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());

        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException(id));
    }

    @Override
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException(id));
        orderItemRepository.delete(orderItem);
    }

    @Override
    public List<OrderItem> getAllByOrderId(Long orderId) {
        // Можно проверить, существует ли заказ, чтобы выбросить исключение
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return orderItemRepository.findByOrderId(orderId);
    }
}


