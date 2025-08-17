package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;

public interface DiscountService {

    boolean hasDiscount(Product product);

    BigDecimal getCurrentPrice(Product product);

    void validateDiscountPrice(Product product, BigDecimal discountPrice);

    BigDecimal calculateDiscountPrice(BigDecimal originalPrice, BigDecimal discountPercentage);
}
