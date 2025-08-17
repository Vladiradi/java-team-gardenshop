package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.ProductEditDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.exception.ProductNotFoundException;
import telran.project.gardenshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder().id(1L).name("Flowers").build();
        testProduct = Product.builder()
                .id(1L)
                .name("Rose")
                .description("Red flower")
                .price(BigDecimal.valueOf(10.0))
                .category(testCategory)
                .build();
    }

    @Test
    void shouldCreateProduct_whenValidProductProvided() {
        Product productToSave = Product.builder()
                .name("Rose")
                .description("Red flower")
                .price(BigDecimal.valueOf(10.0))
                .category(Category.builder().id(1L).build())
                .build();

        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.create(productToSave);

        assertNotNull(result.getId());
        assertEquals("Rose", result.getName());
        assertEquals(testCategory, result.getCategory());

        verify(categoryService).getCategoryById(1L);
        verify(productRepository).save(productToSave);
    }

    @Test
    void shouldReturnProduct_whenProductIdExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Rose", result.getName());
    }

    @Test
    void shouldThrowException_whenProductIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getById(1L));
    }

    @Test
    void shouldUpdateProduct_whenValidUpdateDataProvided() {
        Product existing = Product.builder()
                .id(1L)
                .name("Old name")
                .description("Old desc")
                .price(BigDecimal.valueOf(5))
                .category(testCategory)
                .build();

        ProductEditDto updateDto = new ProductEditDto();
        updateDto.setTitle("New name");
        updateDto.setDescription("New desc");
        updateDto.setPrice(BigDecimal.valueOf(15));

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.update(1L, updateDto);

        assertEquals("New name", result.getName());
        assertEquals("New desc", result.getDescription());
        assertEquals(BigDecimal.valueOf(15), result.getPrice());
    }

    @Test
    void shouldDeleteProduct_whenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.delete(1L);

        verify(productRepository).delete(testProduct);
    }
}
