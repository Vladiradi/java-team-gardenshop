package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
