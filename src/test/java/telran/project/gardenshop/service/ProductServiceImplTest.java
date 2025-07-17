//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import telran.project.gardenshop.entity.Category;
//import telran.project.gardenshop.entity.Product;
//import telran.project.gardenshop.exception.CategoryNotFoundException;
//import telran.project.gardenshop.exception.ProductNotFoundException;
//import telran.project.gardenshop.repository.CategoryRepository;
//import telran.project.gardenshop.repository.ProductRepository;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceImplTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    @Test
//    void testUpdateProduct_Success() {
//        Long productId = 1L;
//        Long categoryId = 10L;
//
//        Product existingProduct = new Product();
//        existingProduct.setId(productId);
//        existingProduct.setName("Old Name");
//
//        Category category = new Category();
//        category.setId(categoryId);
//
//        Product updatedProduct = new Product();
//        updatedProduct.setName("New Name");
//        updatedProduct.setDescription("New Description");
//        updatedProduct.setPrice(99.99);
//        updatedProduct.setImageUrl("new.jpg");
//        updatedProduct.setCategory(category);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
//        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Product result = productService.updateProduct(productId, updatedProduct);
//
//        assertEquals("New Name", result.getName());
//        assertEquals("New Description", result.getDescription());
//        assertEquals(99.99, result.getPrice());
//        assertEquals("new.jpg", result.getImageUrl());
//        assertEquals(category, result.getCategory());
//
//        verify(productRepository).findById(productId);
//        verify(categoryRepository).findById(categoryId);
//        verify(productRepository).save(existingProduct);
//    }
//
//    @Test
//    void testUpdateProduct_ProductNotFound() {
//        Long productId = 1L;
//
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        Product updatedProduct = new Product();
//
//        assertThrows(ProductNotFoundException.class, () ->
//                productService.updateProduct(productId, updatedProduct));
//
//        verify(productRepository).findById(productId);
//        verify(categoryRepository, never()).findById(any());
//        verify(productRepository, never()).save(any());
//    }
//
//    @Test
//    void testUpdateProduct_CategoryNotFound() {
//        Long productId = 1L;
//        Long categoryId = 10L;
//
//        Product existingProduct = new Product();
//        existingProduct.setId(productId);
//
//        Product updatedProduct = new Product();
//        updatedProduct.setCategory(new Category());
//        updatedProduct.getCategory().setId(categoryId);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
//
//        assertThrows(CategoryNotFoundException.class, () ->
//                productService.updateProduct(productId, updatedProduct));
//
//        verify(productRepository).findById(productId);
//        verify(categoryRepository).findById(categoryId);
//        verify(productRepository, never()).save(any());
//    }
//
//    @Test
//    void testDeleteProduct_Success() {
//        Long productId = 1L;
//
//        Product existingProduct = new Product();
//        existingProduct.setId(productId);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
//
//        productService.deleteProduct(productId);
//
//        verify(productRepository).findById(productId);
//        verify(productRepository).delete(existingProduct);
//    }
//
//    @Test
//    void testDeleteProduct_NotFound() {
//        Long productId = 1L;
//
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () ->
//                productService.deleteProduct(productId));
//
//        verify(productRepository).findById(productId);
//        verify(productRepository, never()).delete((Product) any());
//    }
//}