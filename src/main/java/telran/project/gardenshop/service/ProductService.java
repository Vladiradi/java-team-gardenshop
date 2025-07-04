package telran.project.gardenshop.service;
import java.util.List;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Product;

public interface ProductService {
    Product create(ProductRequestDto dto);
    List<Product> getAll();
    Product getById(Long id);
    void delete(Long id);
}