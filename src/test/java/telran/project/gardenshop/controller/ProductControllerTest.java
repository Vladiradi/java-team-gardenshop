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
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.ProductMapper;
import telran.project.gardenshop.service.CategoryService;
import telran.project.gardenshop.service.ProductService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import telran.project.gardenshop.dto.ProductEditDto;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest extends AbstractTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /v1/products - Create product successfully")
    void createProduct_validInput_returnsCreated() throws Exception {
        when(productMapper.toEntity(any(ProductRequestDto.class))).thenReturn(product1);
        when(productService.create(any(Product.class))).thenReturn(product1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productResponseDto1);

        mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(productResponseDto1)));
    }

    @Test
    @DisplayName("GET /v1/products/{id} - Get product by ID")
    void getById_existingId_returnsProduct() throws Exception {
        Long id = 1L;

        when(productService.getById(id)).thenReturn(product1);
        when(productMapper.toDto(product1)).thenReturn(productResponseDto1);

        mockMvc.perform(get("/v1/products/{id}", id))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(productResponseDto1)));
    }

    @Test
    @DisplayName("GET /v1/products - Get all products")
    void getAll_returnsProductList() throws Exception {
        when(productService.getAll()).thenReturn(List.of(product1, product2, product3));
        when(productMapper.toDto(product1)).thenReturn(productResponseDto1);
        when(productMapper.toDto(product2)).thenReturn(productResponseDto2);
        when(productMapper.toDto(product3)).thenReturn(productResponseDto3);

        mockMvc.perform(get("/v1/products"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$[0].id").value(product1.getId()),
                        jsonPath("$[1].id").value(product2.getId()),
                        jsonPath("$[2].id").value(product3.getId()));
    }

    @Test
    @DisplayName("PUT /v1/products/{id} - Update product successfully")
    void updateProduct_validInput_returnsUpdated() throws Exception {
        Long id = 1L;

        when(productService.update(eq(id), any(ProductEditDto.class))).thenReturn(product1);
        when(productMapper.toDto(product1)).thenReturn(productResponseDto1);

        mockMvc.perform(put("/v1/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(productResponseDto1)));
    }

    @Test
    @DisplayName("DELETE /v1/products/{id} - Delete product successfully")
    void deleteProduct_existingId_returnsNoContent() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/v1/products/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
