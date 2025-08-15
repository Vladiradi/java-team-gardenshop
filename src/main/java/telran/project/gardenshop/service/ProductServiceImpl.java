package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import telran.project.gardenshop.dto.ProductEditDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.exception.ProductNotFoundException;
import telran.project.gardenshop.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @Override
    public Product createProduct(Product product) {
        Category category = categoryService.getCategoryById(product.getCategory().getId());

        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = getProductById(id);
        Category category = categoryService.getCategoryById(updatedProduct.getCategory().getId());

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setImageUrl(updatedProduct.getImageUrl());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override

    public Product updateProduct(Long id, ProductEditDto dto) {
        Product product = getProductById(id);

        product.setName(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public Product getProductOfDay() {
        List<Product> productsWithDiscount = productRepository.findAll().stream()
                .filter(product -> product.getDiscountPrice() != null)
                .toList();

        if (productsWithDiscount.isEmpty()) {
            throw new ProductNotFoundException("No products with discount found");
        }

        // Находим максимальную скидку (разницу между обычной ценой и ценой со скидкой)
        double maxDiscount = productsWithDiscount.stream()
                .mapToDouble(product -> product.getPrice().doubleValue() - product.getDiscountPrice())
                .max()
                .orElse(0.0);

        // Фильтруем товары с максимальной скидкой
        List<Product> productsWithMaxDiscount = productsWithDiscount.stream()
                .filter(product -> Math
                        .abs((product.getPrice().doubleValue() - product.getDiscountPrice()) - maxDiscount) < 0.01)
                .toList();

        // Случайный выбор из товаров с максимальной скидкой
        int randomIndex = (int) (Math.random() * productsWithMaxDiscount.size());
        return productsWithMaxDiscount.get(randomIndex);
    }
}
