package telran.project.gardenshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.repository.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    void updateCategory_shouldUpdateAndSave() {
        Long id = 1L;

        CategoryEditDto dto = new CategoryEditDto();
        dto.setName("New Name");
        dto.setDescription(null);

        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("Old Name");
        existingCategory.setDescription("Old Description");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // тут мы не проверяем работу маппера, а имитируем его вызов
        doAnswer(invocation -> {
            CategoryEditDto d = invocation.getArgument(0);
            Category c = invocation.getArgument(1);
            c.setName(d.getName());
            c.setDescription(d.getDescription());
            return null;
        }).when(categoryMapper).updateEntityFromDto(eq(dto), eq(existingCategory));

        Category result = categoryService.updateCategory(id, dto);

        assertEquals("New Name", result.getName());
        assertNull(result.getDescription());
        verify(categoryRepository).save(existingCategory);
    }




}