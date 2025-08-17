package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order getById(Long id);

    List<Order> getForCurrentUser();

    List<Order> getActive();

    List<Order> getAll();

    Order create(OrderCreateRequestDto dto);

    Order cancel(Long id);

    void delete(Long id);

    Order updateOrder(Order order);

    List<Order> getAllByStatus(OrderStatus status);

    List<Order> getAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);

    List<Order> getAllByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime date);
}
