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
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderItemRepository;
import telran.project.gardenshop.repository.OrderRepository;

import java.math.BigDecimal;
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
    public List<Order> getForCurrent() {
        Long userId = userService.getCurrentUser().getId();
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
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public BigDecimal getTotal(Long orderId) {
        Order order = findOrderById(orderId);
        return order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public Order createForCurrent(OrderCreateRequestDto dto) {
        User user = userService.getCurrentUser();
        // cart from current user
        Cart cart = cartService.getOrCreateForCurrentUser();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.NEW)
                .deliveryMethod(dto.getDeliveryMethod().name())
                .deliveryAddress(dto.getAddress())
                .contactName(dto.getContactName())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();
        List<OrderItem> items = orderItemRepository.saveAll(cart.getItems());


//        for (CartItem cartItem : cart.getItems()) {
//            OrderItem orderItem = OrderItem.builder()
//                    .order(order)
//                    .product(cartItem.getProduct())
//                    .quantity(cartItem.getQuantity())
//                    .price(cartItem.getProduct().getPrice())
//                    .build();
//            orderItemRepository.save(orderItem);
//            order.getItems().add(orderItem);
//        }

        order.getItems().addAll(items);
        cartService.clearCurrent();

        return orderRepository.save(order);
    }


    //----------------------
    @Override
    @Transactional
    public Order create(String deliveryAddress, DeliveryMethod deliveryMethod, String contactPhone, Map<Long, Integer> productIdPerQuantityMap) {
        AppUser user = userService.getCurrent();

        Order order = Order.builder()
                .user(user)
                .deliveryAddress(deliveryAddress)
                .contactPhone(contactPhone != null ? contactPhone : user.getPhoneNumber())
                .deliveryMethod(deliveryMethod)
                .build();

        Cart cart = cartService.getByUser(user);
        List<CartItem> cartItems = cart.getItems();

        Map<Long, CartItem> productIdPerCartItemMap = cartItems.stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getProduct().getProductId(), cartItem -> cartItem));

        productIdPerQuantityMap.forEach((productId, quantity) -> {
            if (productIdPerCartItemMap.containsKey(productId)) {
                CartItem cartItem = productIdPerCartItemMap.get(productId);
                order.getItems().add(createOrderItem(quantity, cartItem, order));
                editCartItemList(cartItem, cartItems, quantity);
            }
        });
        checkOrderNotEmpty(order);
        order.setTotalAmount(getTotalAmount(order));

        cartService.update(cart);

        return orderRepository.save(order);
    }
    //----------------------


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
    @Transactional
    public Order createForCurrent(OrderCreateRequestDto dto, Map<Long, Integer> productIdPerQuantityMap) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getOrCreateForCurrentUser();

        List<CartItem> cartItems = cart.getItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }


        Map<Long, CartItem> productIdToCartItem = cartItems.stream()
                .collect(Collectors.toMap(ci -> ci.getProduct().getId(), ci -> ci));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.NEW)
                .deliveryMethod(dto.getDeliveryMethod().name())
                .deliveryAddress(dto.getAddress())
                .contactName(dto.getContactName())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        // ading  only requested items  + adjust cart
        productIdPerQuantityMap.forEach((productId, qtyRequested) -> {
            if (qtyRequested == null || qtyRequested <= 0) {
                return; // skip invalid or zero quantities
            }
            CartItem cartItem = productIdToCartItem.get(productId);
            if (cartItem != null) {
                int qtyToAdd = Math.min(qtyRequested, cartItem.getQuantity());

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(qtyToAdd)
                        .price(cartItem.getProduct().getPrice()) // lock price at order time
                        .build();
                orderItemRepository.save(orderItem);
                order.getItems().add(orderItem);

                // reduce/remove from cart
                editCartItemList(cartItem, cartItems, qtyToAdd);
            }
        });

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Order is empty");
        }

        // changes
        cartService.update(cart);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void delete(Long orderId) {
        Order order = findOrderById(orderId);
        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = findOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancel(Long orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
