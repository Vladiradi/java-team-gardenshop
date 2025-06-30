package telran.project.gardenshop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.exception.CategoryNotFoundException;
import telran.project.gardenshop.exception.ProductNotFoundException;
import telran.project.gardenshop.mapper.ProductMapper;
import telran.project.gardenshop.repository.CategoryRepository;
import telran.project.gardenshop.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto dto) {
        Product product = productMapper.toEntity(dto);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));

        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toDto(product);
    }
}
