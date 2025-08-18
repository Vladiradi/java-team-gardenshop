package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.Cart;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.OrderStatus;
import telran.project.gardenshop.exception.EmptyCartException;
import telran.project.gardenshop.exception.InsufficientQuantityException;
import telran.project.gardenshop.exception.OrderNotFoundException;
import telran.project.gardenshop.exception.ProductNotInCartException;
import telran.project.gardenshop.repository.OrderRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest extends AbstractTest {

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private UserService userService;

        @Mock
        private CartService cartService;

        @Mock
        private ProductService productService;

        @InjectMocks
        private OrderServiceImpl orderService;

        @BeforeEach
        protected void setUp() {
                super.setUp();
        }

        @Test
        void getById_shouldReturnOrder() {
                when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order1));

                Order result = orderService.getById(1L);

                assertThat(result).isEqualTo(order1);
                verify(orderRepository).findById(1L);
        }

        @Test
        void getById_shouldThrowException_whenOrderNotFound() {
                when(orderRepository.findById(999L)).thenReturn(java.util.Optional.empty());

                assertThatThrownBy(() -> orderService.getById(999L))
                                .isInstanceOf(OrderNotFoundException.class);
        }

        @Test
        void getForCurrentUser_shouldReturnUserOrders() {
                when(userService.getCurrent()).thenReturn(user1);
                when(orderRepository.findAllByUserId(1L)).thenReturn(List.of(order1, order3));

                List<Order> result = orderService.getForCurrentUser();

                assertThat(result).hasSize(2);
                assertThat(result).contains(order1, order3);
                verify(orderRepository).findAllByUserId(1L);
        }

        @Test
        void getActive_shouldReturnNonCancelledOrders() {
                when(orderRepository.findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED)))
                                .thenReturn(List.of(order1, order2));

                List<Order> result = orderService.getActive();

                assertThat(result).hasSize(2);
                assertThat(result).contains(order1, order2);
                verify(orderRepository).findAllByStatusNotIn(List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED));
        }

        @Test
        void getAll_shouldReturnAllOrders() {
                when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3, order4));

                List<Order> result = orderService.getAll();

                assertThat(result).hasSize(4);
                verify(orderRepository).findAll();
        }

        @Test
        void create_shouldCreateOrderSuccessfully() {
                when(userService.getCurrent()).thenReturn(user1);
                when(cartService.get()).thenReturn(cart1);
                when(orderRepository.save(any(Order.class))).thenReturn(order1);

                Order result = orderService.create(orderCreateRequestDto);

                assertThat(result).isNotNull();
                verify(cartService).update(cart1);
                verify(orderRepository).save(any(Order.class));
        }

        @Test
        void create_shouldThrowException_whenCartIsEmpty() {
                Cart emptyCart = Cart.builder().id(999L).user(user1).items(List.of()).build();
                when(userService.getCurrent()).thenReturn(user1);
                when(cartService.get()).thenReturn(emptyCart);

                assertThatThrownBy(() -> orderService.create(orderCreateRequestDto))
                                .isInstanceOf(EmptyCartException.class);
        }

        @Test
        void cancel_shouldCancelOrder() {
                when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order1));
                when(orderRepository.save(any(Order.class))).thenReturn(order1);

                Order result = orderService.cancel(1L);

                assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
                verify(orderRepository).save(order1);
        }

        @Test
        void delete_shouldDeleteOrder() {
                when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order1));

                orderService.delete(1L);

                verify(orderRepository).delete(order1);
        }

        @Test
        void updateOrder_shouldUpdateOrder() {
                when(orderRepository.save(any(Order.class))).thenReturn(order1);

                Order result = orderService.updateOrder(order1);

                assertThat(result).isEqualTo(order1);
                verify(orderRepository).save(order1);
        }

        @Test
        void getAllByStatus_shouldReturnOrdersByStatus() {
                when(orderRepository.findAllByStatus(OrderStatus.NEW)).thenReturn(List.of(order1, order2));

                List<Order> result = orderService.getAllByStatus(OrderStatus.NEW);

                assertThat(result).hasSize(2);
                assertThat(result).contains(order1, order2);
                verify(orderRepository).findAllByStatus(OrderStatus.NEW);
        }
}
