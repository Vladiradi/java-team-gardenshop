package telran.project.gardenshop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.service.CategoryService;
import telran.project.gardenshop.service.security.JwtService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private JwtService jwtService;

    @Test
    void createCategory_validInput_returnsCreated() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Flowers");

        Category categoryEntity = Category.builder()
                .name("Flowers")
                .build();

        Category savedCategory = Category.builder()
                .id(1L)
                .name("Flowers")
                .build();

        CategoryResponseDto responseDto = new CategoryResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Flowers");

        when(categoryMapper.toEntity(any(CategoryRequestDto.class))).thenReturn(categoryEntity);
        when(categoryService.createCategory(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Flowers"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Flowers"));
    }

    @Test
    void getCategoryById_existingId_returnsCategory() throws Exception {
        Long id = 1L;

        Category category = Category.builder()
                .id(id)
                .name("Flowers")
                .build();

        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(id);
        dto.setName("Flowers");

        when(categoryService.getCategoryById(id)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        mockMvc.perform(get("/v1/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Flowers"));
    }

    @Test
    void getAllCategories_returnsList() throws Exception {
        Category cat1 = Category.builder().id(1L).name("Fruits").build();
        Category cat2 = Category.builder().id(2L).name("Vegetables").build();

        List<Category> categories = List.of(cat1, cat2);

        CategoryResponseDto dto1 = new CategoryResponseDto();
        dto1.setId(1L);
        dto1.setName("Fruits");

        CategoryResponseDto dto2 = new CategoryResponseDto();
        dto2.setId(2L);
        dto2.setName("Vegetables");

        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toDto(cat1)).thenReturn(dto1);
        when(categoryMapper.toDto(cat2)).thenReturn(dto2);

        mockMvc.perform(get("/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Fruits"))
                .andExpect(jsonPath("$[1].name").value("Vegetables"));
    }

    @Test
    void updateCategory_validInput_returnsUpdated() throws Exception {
        Long id = 1L;

        CategoryEditDto editDto = new CategoryEditDto();
        editDto.setName("New Flowers");
        editDto.setDescription("New description");

        Category updatedCategory = Category.builder()
                .id(id)
                .name("New Flowers")
                .description("New description")
                .build();

        CategoryResponseDto responseDto = new CategoryResponseDto();
        responseDto.setId(id);
        responseDto.setName("New Flowers");

        when(categoryService.updateCategory(eq(id), any(CategoryEditDto.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(responseDto);

        mockMvc.perform(put("/v1/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "New Flowers",
                                "description": "New description"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("New Flowers"));
    }

    @Test
    void deleteCategory_existingId_returnsNoContent() throws Exception {
        Long id = 1L;

        doNothing().when(categoryService).deleteCategory(id);

        mockMvc.perform(delete("/v1/categories/{id}", id))
                .andExpect(status().isNoContent());
    }
}
