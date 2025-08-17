package telran.project.gardenshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.exception.CategoryNotFoundException;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryEditDto testEditDto;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder().id(1L).name("Flowers").description("Beautiful flowers").build();
        testEditDto = new CategoryEditDto();
        testEditDto.setName("New Name");
        testEditDto.setDescription("New description");
    }

    @Test
    void shouldCreateCategory_whenValidCategoryProvided() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertEquals(testCategory, result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldReturnCategory_whenCategoryIdExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.getCategoryById(1L);

        assertEquals(testCategory, result);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void shouldThrowException_whenCategoryIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void shouldUpdateCategory_whenValidEditDtoProvided() {
        Category existingCategory = Category.builder().id(1L).name("Old Name").description("Old desc").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        doAnswer(invocation -> {
            CategoryEditDto source = invocation.getArgument(0);
            Category target = invocation.getArgument(1);
            target.setName(source.getName());
            target.setDescription(source.getDescription());
            return null;
        }).when(categoryMapper).updateEntityFromDto(eq(testEditDto), eq(existingCategory));

        Category result = categoryService.updateCategory(1L, testEditDto);

        assertEquals("New Name", result.getName());
        assertEquals("New description", result.getDescription());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldReturnAllCategories_whenCategoriesExist() {
        Category category1 = Category.builder().id(1L).name("Fruits").build();
        Category category2 = Category.builder().id(2L).name("Vegetables").build();
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldDeleteCategory_whenCategoryExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistentCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }
}
