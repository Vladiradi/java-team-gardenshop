package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.*;
import telran.project.gardenshop.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(nullable = false)
    private BigDecimal discountValue; // Процент или фиксированная сумма

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Проверяет, активна ли скидка в данный момент
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive &&
                now.isAfter(startDate) &&
                now.isBefore(endDate);
    }

    /**
     * Вычисляет цену со скидкой
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        if (!isCurrentlyActive()) {
            return originalPrice;
        }

        switch (type) {
            case PERCENTAGE:
                BigDecimal percentageMultiplier = BigDecimal.ONE.subtract(
                        discountValue.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP));
                return originalPrice.multiply(percentageMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);

            case FIXED_AMOUNT:
                BigDecimal discountedPrice = originalPrice.subtract(discountValue);
                return discountedPrice.compareTo(BigDecimal.ZERO) > 0 ? discountedPrice : BigDecimal.ZERO;

            case PRODUCT_OF_DAY:
                // Товар дня обычно имеет фиксированную скидку 20%
                BigDecimal dayProductMultiplier = BigDecimal.valueOf(0.8);
                return originalPrice.multiply(dayProductMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);

            default:
                return originalPrice;
        }
    }
}
