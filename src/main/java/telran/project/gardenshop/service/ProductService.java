package telran.project.gardenshop.service;

import java.util.List;

import telran.project.gardenshop.dto.ProductEditDto;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;

import telran.project.gardenshop.entity.Product;

public interface ProductService {
    Product createProduct(Product product);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product updateProduct(Long id, Product product);

    Product updateProduct(Long id, ProductEditDto dto);

    void deleteProduct(Long id);
}
