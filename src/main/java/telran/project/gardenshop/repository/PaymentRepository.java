package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}