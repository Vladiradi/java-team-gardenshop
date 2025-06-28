package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.project.gardenshop.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
