package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Payment;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    //List<Payment> findAllByOrderId(Long orderId);
}
