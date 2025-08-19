package telran.project.gardenshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import telran.project.gardenshop.AbstractTest;
import telran.project.gardenshop.dto.CategoryEditDto;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;
import telran.project.gardenshop.mapper.CategoryMapper;
import telran.project.gardenshop.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest extends AbstractTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryController categoryController;

    private ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /v1/categories - Create category successfully")
    void createCategory_validInput_returnsCreated() throws Exception {
        when(categoryMapper.toEntity(any(CategoryRequestDto.class))).thenReturn(categoryToCreate);
        when(categoryService.createCategory(any(Category.class))).thenReturn(categoryCreated);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryResponseCreatedDto);

        mockMvc.perform(post("/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(categoryResponseCreatedDto)));
    }

    @Test
    @DisplayName("GET /v1/categories/{id} - Get category by ID")
    void getCategoryById_existingId_returnsCategory() throws Exception {
        Long id = 1L;

        when(categoryService.getCategoryById(id)).thenReturn(category1);
        when(categoryMapper.toDto(category1)).thenReturn(categoryResponseDto1);

        mockMvc.perform(get("/v1/categories/{id}", id))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(categoryResponseDto1)));
    }

    @Test
    @DisplayName("GET /v1/categories - Get all categories")
    void getAllCategories_returnsList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(category1, category2, category3));
        when(categoryMapper.toDto(category1)).thenReturn(categoryResponseDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryResponseDto2);
        when(categoryMapper.toDto(category3)).thenReturn(categoryResponseDto3);

        mockMvc.perform(get("/v1/categories"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(3),
                        jsonPath("$[0].name").value(category1.getName()),
                        jsonPath("$[1].name").value(category2.getName()),
                        jsonPath("$[2].name").value(category3.getName()));
    }

    @Test
    @DisplayName("PUT /v1/categories/{id} - Update category successfully")
    void updateCategory_validInput_returnsUpdated() throws Exception {
        Long id = 1L;

        CategoryEditDto editDto = new CategoryEditDto();
        editDto.setName("Updated Category");
        editDto.setDescription("Updated description");

        Category updatedCategory = Category.builder()
                .id(id)
                .name(editDto.getName())
                .description(editDto.getDescription())
                .build();

        CategoryResponseDto responseDto = new CategoryResponseDto();
        responseDto.setId(id);
        responseDto.setName(editDto.getName());

        when(categoryService.updateCategory(eq(id), any(CategoryEditDto.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(responseDto);

        mockMvc.perform(put("/v1/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDto)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("DELETE /v1/categories/{id} - Delete category successfully")
    void deleteCategory_existingId_returnsNoContent() throws Exception {
        Long id = 1L;

        doNothing().when(categoryService).deleteCategory(id);

        mockMvc.perform(delete("/v1/categories/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
