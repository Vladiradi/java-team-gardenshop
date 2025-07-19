package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.OrderItem;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.exception.OrderItemNotFoundException;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.repository.OrderItemRepository;
import telran.project.gardenshop.repository.OrderRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceImplTest {

    @InjectMocks
    private OrderItemServiceImpl service;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    private Order order;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);

        product = new Product();
        product.setId(100L);
        product.setPrice(50.0);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
    }

    @Test
    void createOrderItem_success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        OrderItem result = service.createOrderItem(orderItem);

        assertNotNull(result);
        assertEquals(product.getPrice(), result.getPrice());

        verify(orderRepository).findById(order.getId());
        verify(productService).getProductById(product.getId());
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void getOrderItemById_success() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

        OrderItem result = service.getOrderItemById(1L);

        assertEquals(orderItem, result);
        verify(orderItemRepository).findById(1L);
    }

    @Test
    void getOrderItemById_notFound_throwsException() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderItemNotFoundException.class,
                () -> service.getOrderItemById(1L));
    }

    @Test
    void deleteOrderItem_success() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

        service.deleteOrderItem(1L);

        verify(orderItemRepository).delete(orderItem);
    }

    @Test
    void deleteOrderItem_notFound_throwsException() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderItemNotFoundException.class,
                () -> service.deleteOrderItem(1L));
    }

    @Test
    void getAllByOrderId_success() {
        List<OrderItem> expectedItems = List.of(orderItem);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(expectedItems);

        List<OrderItem> result = service.getAllByOrderId(1L);

        assertEquals(expectedItems, result);
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).findByOrderId(1L);
    }

    @Test
    void getAllByOrderId_orderNotFound_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> service.getAllByOrderId(1L));

        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderItemRepository);
    }
}
