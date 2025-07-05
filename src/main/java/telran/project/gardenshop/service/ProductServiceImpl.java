package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.common.fetcher.EntityFetcher;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.repository.CategoryRepository;
import telran.project.gardenshop.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EntityFetcher entityFetcher;

    @Override
    public Product createProduct(Product product) {
        Category category = getCategoryByIdOrThrow(product.getCategory().getId());
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return findProductById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = findProductById(id);
        Category category = getCategoryByIdOrThrow(updatedProduct.getCategory().getId());

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setImageUrl(updatedProduct.getImageUrl());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return entityFetcher.fetchOrThrow(productRepository, id, "Product");
    }

    private Category getCategoryByIdOrThrow(Long categoryId) {
        return entityFetcher.fetchOrThrow(categoryRepository, categoryId, "Category");
    }
}