//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import telran.project.gardenshop.dto.OrderCreateRequestDto;
//import telran.project.gardenshop.entity.Order;
//import telran.project.gardenshop.entity.OrderItem;
//import telran.project.gardenshop.entity.User;
//import telran.project.gardenshop.enums.DeliveryMethod;
//import telran.project.gardenshop.repository.OrderItemRepository;
//import telran.project.gardenshop.repository.OrderRepository;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceImplTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock private OrderItemRepository orderItemRepository;
//    @Mock private ProductService productService;
//    @Mock private UserService userService;
//
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    @Test
//    void testGetOrderById() {
//        Order order = Order.builder().id(1L).build();
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        Order result = orderService.getOrderById(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void testCreateOrder() {
//        User user = new User();
//        user.setId(1L);
//        OrderCreateRequestDto dto = new OrderCreateRequestDto();
//        dto.setAddress("Address");
//        dto.setContactName("Test");
//        dto.setCreatedAt(LocalDateTime.now());
//        dto.setDeliveryMethod(DeliveryMethod.COURIER);
//
//        Order order = Order.builder().id(1L).user(user).build();
//
//        when(userService.getUserById(1L)).thenReturn(user);
//        when(orderRepository.save(any())).thenReturn(order);
//
//        Order result = orderService.createOrder(1L, dto);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void testGetTotalAmount() {
//        OrderItem item1 = OrderItem.builder()
//                .price(BigDecimal.valueOf(10))
//                .quantity(2)
//                .build();
//        OrderItem item2 = OrderItem.builder()
//                .price(BigDecimal.valueOf(5))
//                .quantity(3)
//                .build();
//
//        Order order = Order.builder()
//                .id(1L)
//                .items(List.of(item1, item2))
//                .build();
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        BigDecimal result = orderService.getTotalAmount(1L);
//
//        assertEquals(BigDecimal.valueOf(35), result);
//    }
//}
