package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto addProduct(ProductRequestDto dto);
    List<ProductResponseDto> getAllProducts();
    ProductResponseDto getProductById(Long id);
}
