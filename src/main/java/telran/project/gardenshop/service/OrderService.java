package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    Order getById(Long id);
    List<Order> getForCurrentUser();
    List<Order> getByUserId(Long userId);
    List<Order> getActive();
    List<Order> getAll();

    Order create(OrderCreateRequestDto dto);
    Order updateStatus(Long id, OrderStatus status);
    Order cancel(Long id);

    void delete(Long id);
}
