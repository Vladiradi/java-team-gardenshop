package telran.project.gardenshop.service;

import org.springframework.stereotype.Service;
import telran.project.gardenshop.entity.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Override
    public boolean hasDiscount(Product product) {
        return product.getDiscountPrice() != null &&
                product.getDiscountPrice().compareTo(product.getPrice()) < 0;
    }

    @Override
    public BigDecimal getCurrentPrice(Product product) {
        return hasDiscount(product) ? product.getDiscountPrice() : product.getPrice();
    }

    @Override
    public BigDecimal getDiscountPercentage(Product product) {
        if (!hasDiscount(product)) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountAmount = product.getPrice().subtract(product.getDiscountPrice());
        return discountAmount.divide(product.getPrice(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public BigDecimal getDiscountAmount(Product product) {
        if (!hasDiscount(product)) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().subtract(product.getDiscountPrice());
    }

    @Override
    public void validateDiscountPrice(Product product, BigDecimal discountPrice) {
        if (discountPrice != null && discountPrice.compareTo(product.getPrice()) >= 0) {
            throw new IllegalArgumentException("Discount price must be less than regular price");
        }
    }

    @Override
    public BigDecimal calculateDiscountPrice(BigDecimal originalPrice, BigDecimal discountPercentage) {
        BigDecimal discountAmount = originalPrice
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return originalPrice.subtract(discountAmount);
    }
}
