package telran.project.gardenshop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    private Double discountPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Discount> discounts = new ArrayList<>();

    public List<Favorite> getFavorites() {
        return favorites != null ? favorites : new ArrayList<>();
    }

    public List<Discount> getDiscounts() {
        return discounts != null ? discounts : new ArrayList<>();
    }

    /**
     * Получает активную скидку для товара
     */
    public Discount getActiveDiscount() {
        return getDiscounts().stream()
                .filter(Discount::isCurrentlyActive)
                .findFirst()
                .orElse(null);
    }

    /**
     * Получает цену со скидкой
     */
    public BigDecimal getDiscountedPrice() {
        Discount activeDiscount = getActiveDiscount();
        if (activeDiscount != null) {
            return activeDiscount.calculateDiscountedPrice(price);
        }
        return price;
    }

    /**
     * Проверяет, является ли товар товаром дня
     */
    public boolean isProductOfDay() {
        return getDiscounts().stream()
                .anyMatch(
                        discount -> discount.getType() == telran.project.gardenshop.enums.DiscountType.PRODUCT_OF_DAY &&
                                discount.isCurrentlyActive());
    }
}