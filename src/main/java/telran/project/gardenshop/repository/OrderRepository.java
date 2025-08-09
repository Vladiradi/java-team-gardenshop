package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Order;
import telran.project.gardenshop.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStatusNotIn(Collection<OrderStatus> statuses);

    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByStatus(OrderStatus status);
    
    List<Order> findAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);
    
    List<Order> findAllByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime date);
}
