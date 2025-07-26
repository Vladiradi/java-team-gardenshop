package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findAllByStatusNotIn(Collection<OrderStatus> statuses);

    List<Order> findAllByUserId(Long userId);
    //this use for scheduler , ok
    List<Order> findAllByStatus(OrderStatus status);
}
