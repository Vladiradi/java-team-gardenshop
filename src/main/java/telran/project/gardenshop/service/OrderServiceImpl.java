package telran.project.gardenshop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.EmptyCartException;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserService userService;

    private final CartService cartService;

    private final ProductService productService;

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
    public List<Order> getActive() {
        return orderRepository.findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED));
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Order create(OrderCreateRequestDto dto) {
        User user = userService.getCurrent();
        Cart cart = cartService.get();

        validateCartNotEmpty(cart);

        Order order = buildOrder(user, dto);

        Map<Long, CartItem> productIdPerCartItemMap = cart.getItems().stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getProduct().getId(), cartItem -> cartItem));

        dto.getItems().forEach(itemDto -> {
            if (productIdPerCartItemMap.containsKey(itemDto.getProductId())) {
                CartItem cartItem = productIdPerCartItemMap.get(itemDto.getProductId());

                int requestedQuantity = itemDto.getQuantity();
                int availableQuantity = cartItem.getQuantity();
                int quantityToTake = Math.min(requestedQuantity, availableQuantity);

                if (quantityToTake > 0) {
                    OrderItem orderItem = createOrderItem(quantityToTake, cartItem, order);
                    order.getItems().add(orderItem);

                    if (requestedQuantity > availableQuantity) {
                        log.debug("Product {}: Requested {} > available {}. Taking all available {} items.",
                                cartItem.getProduct().getName(), requestedQuantity, availableQuantity, quantityToTake);
                    } else if (requestedQuantity < availableQuantity) {
                        log.debug("Product {}: Requested {} < available {}. Taking {} items, {} remain in cart.",
                                cartItem.getProduct().getName(), requestedQuantity, availableQuantity, quantityToTake,
                                availableQuantity - quantityToTake);
                    } else {
                        log.debug("Product {}: Requested {} = available {}. Taking all {} items.",
                                cartItem.getProduct().getName(), requestedQuantity, availableQuantity, quantityToTake);
                    }

                    editCartItemList(cartItem, cart.getItems(), quantityToTake);
                } else {
                    log.debug("Product {}: Cannot create order item - requested {} but available {}.",
                            cartItem.getProduct().getName(), requestedQuantity, availableQuantity);
                }
            } else {
                log.debug("Product with ID {} not found in cart. Skipping order item creation.",
                        itemDto.getProductId());
            }
        });

        cartService.update(cart);

        log.debug("Order created for user {}: requested {} items, actual {} items in order",
                user.getEmail(), dto.getItems().size(), order.getItems().size());

        return orderRepository.save(order);
    }

    @Override
    public void delete(Long orderId) {
        Order order = findOrderById(orderId);
        orderRepository.delete(order);
    }

    @Override
    public Order cancel(Long orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private void validateCartNotEmpty(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot create an order with an empty cart");
        }
    }

    private Order buildOrder(User user, OrderCreateRequestDto dto) {
        return Order.builder()
                .user(user)
                .deliveryMethod(dto.getDeliveryMethod().name())
                .deliveryAddress(dto.getDeliveryAddress())
                .contactName(user.getFullName())
                .build();
    }

    private OrderItem createOrderItem(int quantity, CartItem cartItem, Order order) {
        return OrderItem.builder()
                .order(order)
                .product(cartItem.getProduct())
                .quantity(quantity)
                .price(productService.getCurrentPrice(cartItem.getProduct()))
                .build();
    }

    private void editCartItemList(CartItem cartItem, List<CartItem> cartItems, int quantityToTake) {
        if (cartItem.getQuantity() <= quantityToTake) {
            cartItems.remove(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - quantityToTake);
        }
    }

    @Override
    public List<Order> getAllByStatus(OrderStatus status) {
        return orderRepository.findAllByStatus(status);
    }

    @Override
    public List<Order> getAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate,
            OrderStatus status) {
        return orderRepository.findAllByCreatedAtBetweenAndStatus(startDate, endDate, status);
    }

    @Override
    public List<Order> getAllByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime date) {
        return orderRepository.findAllByStatusAndCreatedAtBefore(status, date);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

}
