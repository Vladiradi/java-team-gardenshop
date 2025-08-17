package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.Category;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,  JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE p.discountPrice IS NOT NULL " +
           "AND p.discountPrice < p.price " +
           "AND (p.price - p.discountPrice) = (" +
           "SELECT MAX(p2.price - p2.discountPrice) " +
           "FROM Product p2" +
           ")")
    List<Product> findProductsWithHighestDiscount();

    @Query("SELECT p FROM Product p WHERE p.discountPrice IS NOT NULL " +
           "AND p.discountPrice is not NULL AND p.discountPrice < p.price")
    List<Product> findProductsWithDiscount();

    @Query("SELECT p FROM Product p WHERE p.discountPrice IS NOT NULL " +
           "AND p.discountPrice is not NULL AND p.discountPrice < p.price " +
           "AND p.category = :category")
    List<Product> findProductsWithDiscountByCategory(Category category);
}
