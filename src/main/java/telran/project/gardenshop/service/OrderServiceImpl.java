package telran.project.gardenshop.service;

import jakarta.transaction.Transactional;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderItemRepository;
import telran.project.gardenshop.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartItemService cartItemService;

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId((userId));
    }

    @Override
    public List<Order> getActiveOrders() {
        return orderRepository.findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED));
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public BigDecimal getTotalAmount(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequestDto dto) {
        User user = userService.getUserById(userId);
        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.NEW)
                .deliveryMethod(dto.getDeliveryMethod().name())
                .deliveryAddress(dto.getAddress())
                .contactName(dto.getContactName())
                .createdAt(dto.getCreatedAt())
                .items(new ArrayList<>())
                .build();
        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);
        }

        cartItemService.clearCart(cart.getId());

        return order;
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);
        orderRepository.delete(order);
    }


    @Override
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Order addItem(Long orderId, Long productId, Integer quantity) {
        Order order = getOrderById(orderId);
        Product product = productService.getProductById(productId);
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
        order.getItems().add(item);
        orderItemRepository.save(item);
        return orderRepository.save(order);
    }

    @Override
    public Order updateItem(Long orderItemId, Integer quantity) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new OrderNotFoundException(orderItemId));
        item.setQuantity(quantity);
        return orderRepository.save(item.getOrder());
    }

    @Override
    public Order removeItem(Long orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new OrderNotFoundException(orderItemId));
        Order order = item.getOrder();
        order.getItems().remove(item);
        orderItemRepository.delete(item);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}