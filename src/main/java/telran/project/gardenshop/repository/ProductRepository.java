package telran.project.gardenshop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import telran.project.gardenshop.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>,  JpaSpecificationExecutor<Product> {
}