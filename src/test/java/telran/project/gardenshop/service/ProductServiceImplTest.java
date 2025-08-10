//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import telran.project.gardenshop.entity.Category;
//import telran.project.gardenshop.entity.Product;
//import telran.project.gardenshop.exception.ProductNotFoundException;
//import telran.project.gardenshop.repository.ProductRepository;
//import java.math.BigDecimal;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceImplTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private CategoryService categoryService;
//
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    @Test
//    void createProduct_shouldSaveProductWithCategory() {
//        Category category = Category.builder().id(1L).name("Flowers").build();
//
//        Product productToSave = Product.builder()
//                .name("Rose")
//                .description("Red flower")
//                .price(BigDecimal.valueOf(10.0))
//                .category(Category.builder().id(1L).build()) // только id для запроса
//                .build();
//
//        Product savedProduct = Product.builder()
//                .id(1L)
//                .name("Rose")
//                .description("Red flower")
//                .price(BigDecimal.valueOf(10.0))
//                .category(category)
//                .build();
//
//        when(categoryService.getCategoryById(1L)).thenReturn(category);
//        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
//
//        Product result = productService.createProduct(productToSave);
//
//        assertNotNull(result.getId());
//        assertEquals("Rose", result.getName());
//        assertEquals(category, result.getCategory());
//
//        verify(categoryService).getCategoryById(1L);
//        verify(productRepository).save(productToSave);
//    }
//
//    @Test
//    void getProductById_existingId_shouldReturnProduct() {
//        Product product = Product.builder()
//                .id(1L)
//                .name("Rose")
//                .build();
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        Product result = productService.getProductById(1L);
//
//        assertEquals(1L, result.getId());
//        assertEquals("Rose", result.getName());
//    }
//
//    @Test
//    void getProductById_nonExistingId_shouldThrowException() {
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
//    }
//
//    @Test
//    void updateProduct_shouldUpdateFields() {
//        Category category = Category.builder().id(2L).name("Trees").build();
//
//        Product existing = Product.builder()
//                .id(1L)
//                .name("Old name")
//                .description("Old desc")
//                .price(BigDecimal.valueOf(5))
//                .category(Category.builder().id(1L).build())
//                .build();
//
//        Product updated = Product.builder()
//                .name("New name")
//                .description("New desc")
//                .price(BigDecimal.valueOf(15))
//                .imageUrl("url")
//                .category(Category.builder().id(2L).build())
//                .build();
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
//
//        when(categoryService.getCategoryById(2L)).thenReturn(category);
//
//        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
//
//        Product result = productService.updateProduct(1L, updated);
//
//        assertEquals("New name", result.getName());
//
//        assertEquals("New desc", result.getDescription());
//
//        assertEquals(BigDecimal.valueOf(15), result.getPrice());
//
//        assertEquals("url", result.getImageUrl());
//
//        assertEquals(category, result.getCategory());
//    }
//
//    @Test
//    void deleteProduct_existingId_shouldDelete() {
//        Product product = Product.builder().id(1L).build();
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        productService.deleteProduct(1L);
//
//        verify(productRepository).delete(product);
//    }
//
//}
