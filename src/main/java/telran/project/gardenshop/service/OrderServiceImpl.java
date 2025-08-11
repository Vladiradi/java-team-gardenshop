package telran.project.gardenshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.EmptyCartException;
import telran.project.gardenshop.exception.InsufficientQuantityException;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.exception.ProductNotInCartException;
import telran.project.gardenshop.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserService userService;

    private final CartService cartService;

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
        List<OrderItem> orderItems = createOrderItemsFromCart(dto, cart, order);

        order.getItems().addAll(orderItems);

        cartService.update(cart);
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

    private List<OrderItem> createOrderItemsFromCart(OrderCreateRequestDto dto, Cart cart, Order order) {
        return dto.getItems().stream()
                .map(itemDto -> processOrderItem(itemDto, cart, order))
                .toList();
    }

    private OrderItem processOrderItem(OrderItemRequestDto itemDto, Cart cart, Order order) {
        CartItem cartItem = findCartItemByProductId(cart.getItems(), itemDto.getProductId()).orElseThrow(() -> new ProductNotInCartException(itemDto.getProductId()));

        validateQuantity(cartItem, itemDto);

        OrderItem orderItem = OrderItem.builder().order(order).product(cartItem.getProduct()).quantity(itemDto.getQuantity()).price(cartItem.getProduct().getPrice()).build();

        editCartItemList(cartItem, cart.getItems(), itemDto.getQuantity());

        return orderItem;
    }

    private void validateQuantity(CartItem cartItem, OrderItemRequestDto itemDto) {
        if (cartItem.getQuantity() < itemDto.getQuantity()) {
            throw new InsufficientQuantityException(itemDto.getProductId(), cartItem.getQuantity(), itemDto.getQuantity());
        }
    }

    private void editCartItemList(CartItem cartItem, List<CartItem> cartItems, int quantityToTake) {
        if (cartItem.getQuantity() <= quantityToTake) {
            cartItems.remove(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - quantityToTake);
        }
    }

    private Optional<CartItem> findCartItemByProductId(List<CartItem> cartItems, Long productId) {
        return cartItems.stream().filter(ci -> ci.getProduct().getId().equals(productId)).findFirst();
    }

}
