package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import telran.project.gardenshop.dto.ProductDiscountDto;
import telran.project.gardenshop.dto.ProductEditDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.exception.ProductNotFoundException;
import telran.project.gardenshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import telran.project.gardenshop.exception.NoDiscountedProductsException;
import telran.project.gardenshop.exception.InvalidDiscountPriceException;
import telran.project.gardenshop.exception.InvalidDiscountDataException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @Override
    public Product create(Product product) {
        Category category = categoryService.getCategoryById(product.getCategory().getId());

        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product update(Long id, ProductEditDto dto) {
        Product product = getById(id);

        product.setName(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    @Override
    public Product addDiscount(Long productId, ProductDiscountDto discountDto) {
        if (discountDto.getDiscountPrice() ==  null && discountDto.getDiscountPercentage() == null) {
            throw new InvalidDiscountDataException("Either discountPrice or discountPercentage must be provided");
        }

        Product product = getById(productId);
        BigDecimal discountPrice;
        if (discountDto.getDiscountPrice() != null) {
            discountPrice = BigDecimal.valueOf(discountDto.getDiscountPrice());
            validateDiscountPrice(product, discountPrice);
        } else {
            BigDecimal discountPercentage = BigDecimal.valueOf(discountDto.getDiscountPercentage());
            discountPrice = calculateDiscountPrice(product.getPrice(), discountPercentage);
        }
        product.setDiscountPrice(discountPrice);

        return productRepository.save(product);
    }

    @Override
    public Product removeDiscount(Long productId) {
        Product product = getById(productId);
        product.setDiscountPrice(null);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsWithDiscount() {
        return productRepository.findProductsWithDiscount();
    }

    @Override
    public List<Product> getProductsWithDiscount(Category category) {
        if (category == null) {
            return getProductsWithDiscount();
        }
        return productRepository.findProductsWithDiscountByCategory(category);
    }

    @Override
    public Product getProductOfTheDay() {
        List<Product> productsWithHighestDiscount = productRepository.findProductsWithHighestDiscount();

        if (productsWithHighestDiscount.isEmpty()) {
            throw new NoDiscountedProductsException("No discounted products available");
        }

        int randomIndex = new Random().nextInt(productsWithHighestDiscount.size());
        return productsWithHighestDiscount.get(randomIndex);
    }

    @Override
    public BigDecimal getCurrentPrice(Product product) {
        return hasDiscount(product) ? product.getDiscountPrice() : product.getPrice();
    }

    private boolean hasDiscount(Product product) {
        return product.getDiscountPrice() != null &&
                product.getDiscountPrice().doubleValue() < product.getPrice().doubleValue();
    }

    private void validateDiscountPrice(Product product, BigDecimal discountPrice) {
        if (discountPrice != null && discountPrice.compareTo(product.getPrice()) >= 0) {
            throw new InvalidDiscountPriceException("Discount price must be less than regular price");
        }
    }

    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice, BigDecimal discountPercentage) {
        BigDecimal discountAmount = originalPrice
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return originalPrice.subtract(discountAmount);
    }
}
