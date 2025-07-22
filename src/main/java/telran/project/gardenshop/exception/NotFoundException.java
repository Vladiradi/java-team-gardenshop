package telran.project.gardenshop.exception;
import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}