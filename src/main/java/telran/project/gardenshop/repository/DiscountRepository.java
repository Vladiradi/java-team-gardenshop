package telran.project.gardenshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telran.project.gardenshop.entity.Discount;
import telran.project.gardenshop.enums.DiscountType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    /**
     * Находит все активные скидки для товара
     */
    @Query("SELECT d FROM Discount d WHERE d.product.id = :productId AND d.isActive = true")
    List<Discount> findActiveDiscountsByProductId(@Param("productId") Long productId);

    /**
     * Находит все активные скидки определенного типа
     */
    @Query("SELECT d FROM Discount d WHERE d.type = :type AND d.isActive = true")
    List<Discount> findActiveDiscountsByType(@Param("type") DiscountType type);

    /**
     * Находит товар дня (активная скидка типа PRODUCT_OF_DAY)
     */
    @Query("SELECT d FROM Discount d WHERE d.type = 'PRODUCT_OF_DAY' AND d.isActive = true " +
            "AND :now BETWEEN d.startDate AND d.endDate")
    Optional<Discount> findProductOfDay(@Param("now") LocalDateTime now);

    /**
     * Находит все активные скидки в данный момент
     */
    @Query("SELECT d FROM Discount d WHERE d.isActive = true " +
            "AND :now BETWEEN d.startDate AND d.endDate")
    List<Discount> findCurrentlyActiveDiscounts(@Param("now") LocalDateTime now);

    /**
     * Находит скидки, которые истекают в ближайшие N дней
     */
    @Query("SELECT d FROM Discount d WHERE d.isActive = true " +
            "AND d.endDate BETWEEN :now AND :futureDate")
    List<Discount> findExpiringDiscounts(@Param("now") LocalDateTime now,
            @Param("futureDate") LocalDateTime futureDate);
}
