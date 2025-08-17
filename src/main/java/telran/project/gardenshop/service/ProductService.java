package telran.project.gardenshop.service;

import java.util.List;
import java.math.BigDecimal;

import telran.project.gardenshop.dto.ProductDiscountDto;
import telran.project.gardenshop.dto.ProductEditDto;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.Category;

public interface ProductService {

    Product create(Product product);

    Product getById(Long id);

    List<Product> getAll();

    Product update(Long id, ProductEditDto dto);

    void delete(Long id);

    // Методы для управления скидками
    Product addDiscount(Long productId, ProductDiscountDto discountDto);

    Product removeDiscount(Long productId);

    List<Product> getProductsWithDiscount();

    List<Product> getProductsWithDiscount(Category category);

    Product getProductOfTheDay();

    BigDecimal getCurrentPrice(Product product);
}
