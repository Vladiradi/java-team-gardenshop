package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
