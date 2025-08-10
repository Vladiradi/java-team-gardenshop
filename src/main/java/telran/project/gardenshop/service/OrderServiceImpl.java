package telran.project.gardenshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.CartItem;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.DeliveryMethod;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderItemRepository;
import telran.project.gardenshop.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final CartService cartService;

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public Order getById(Long orderId) {
        return findOrderById(orderId);
    }

    @Override
    public List<Order> getForCurrentUser() {
        Long userId = userService.getCurrent().getId();
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    public List<Order> getByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    public List<Order> getActive() {
        return orderRepository.findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED));
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Order createForCurrentUser(OrderCreateRequestDto dto) {
        User user = userService.getCurrent();
        // cart from current user
        Cart cart = cartService.get();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.NEW)
                .deliveryMethod(dto.getDeliveryMethod().name())
                .deliveryAddress(dto.getAddress())
                .contactName(dto.getContactName())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        List<OrderItem> items = cart.getItems().stream()
                .map(i -> OrderItem.builder().order(order)
                .product(i.getProduct())
                .quantity(i.getQuantity())
                .price(i.getProduct().getPrice())
                .build()).toList();

        cartService.deleteItems(cart.getItems());

        items = orderItemRepository.saveAll(items);
        order.getItems().addAll(items);

        return orderRepository.save(order);
    }

    private void editCartItemList(CartItem cartItem, List<CartItem> cartItems, int quantityToTake) {
        if (cartItem.getQuantity() <= quantityToTake) {
            cartItems.remove(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - quantityToTake);
        }
    }

    private Optional<CartItem> findCartItemByProductId(List<CartItem> cartItems, Long productId) {
        return cartItems.stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst();
    }

    @Override
    public void delete(Long orderId) {
        Order order = findOrderById(orderId);
        orderRepository.delete(order);
    }

    @Override
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = findOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Order cancel(Long orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
