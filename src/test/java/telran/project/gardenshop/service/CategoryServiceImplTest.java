//package telran.project.gardenshop.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import telran.project.gardenshop.dto.CategoryEditDto;
//import telran.project.gardenshop.entity.Category;
//import telran.project.gardenshop.exception.CategoryNotFoundException;
//import telran.project.gardenshop.mapper.CategoryMapper;
//import telran.project.gardenshop.repository.CategoryRepository;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CategoryServiceImplTest {
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private CategoryMapper categoryMapper;
//
//    @InjectMocks
//    private CategoryServiceImpl categoryService;
//
//    @Test
//    void testCreateCategory() {
//        Category category = Category.builder().id(1L).name("Flowers").build();
//        when(categoryRepository.save(any(Category.class))).thenReturn(category);
//
//        Category result = categoryService.createCategory(category);
//
//        assertEquals(category, result);
//        verify(categoryRepository).save(any(Category.class));
//    }
//
//    @Test
//    void testGetCategoryByIdFound() {
//        Category category = Category.builder().id(1L).name("Flowers").build();
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//
//        Category result = categoryService.getCategoryById(1L);
//
//        assertEquals(category, result);
//        verify(categoryRepository).findById(1L);
//    }
//
//    @Test
//    void testGetCategoryByIdNotFound() {
//        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1L));
//    }
//
//    @Test
//    void testUpdateCategory() {
//        Category existing = Category.builder().id(1L).name("Old Name").description("Old desc").build();
//        CategoryEditDto dto = new CategoryEditDto();
//        dto.setName("New Name");
//        dto.setDescription("New desc");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
//        when(categoryRepository.save(any(Category.class))).thenReturn(existing);
//
//        doAnswer(invocation -> {
//            CategoryEditDto source = invocation.getArgument(0);
//            Category target = invocation.getArgument(1);
//            target.setName(source.getName());
//            target.setDescription(source.getDescription());
//            return null;
//        }).when(categoryMapper).updateEntityFromDto(eq(dto), eq(existing));
//
//        Category result = categoryService.updateCategory(1L, dto);
//
//        assertEquals("New Name", result.getName());
//        assertEquals("New desc", result.getDescription());
//        verify(categoryRepository).save(any(Category.class));
//    }
//
//    @Test
//    void testGetAllCategories() {
//        Category cat1 = Category.builder().id(1L).name("Fruits").build();
//        Category cat2 = Category.builder().id(2L).name("Vegetables").build();
//
//        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));
//
//        List<Category> result = categoryService.getAllCategories();
//
//        assertEquals(2, result.size());
//        verify(categoryRepository).findAll();
//    }
//
//    @Test
//    void testDeleteCategory() {
//        Category category = Category.builder().id(1L).name("ToDelete").build();
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//
//        categoryService.deleteCategory(1L);
//
//        verify(categoryRepository).delete(category);
//    }
//
//    @Test
//    void testDeleteCategoryNotFound() {
//        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1L));
//    }
//}
