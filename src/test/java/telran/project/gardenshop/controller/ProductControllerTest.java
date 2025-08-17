package telran.project.gardenshop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.project.gardenshop.dto.ProductRequestDto;
import telran.project.gardenshop.dto.ProductResponseDto;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.ProductMapper;
import telran.project.gardenshop.repository.ProductRepository;
import telran.project.gardenshop.service.ProductService;
import telran.project.gardenshop.service.security.JwtService;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)  // disable security
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private JwtService jwtService;

    @Test
    void createProduct_validInput_returnsCreated() throws Exception {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("Rose")
                .description("Red flower")
                .price(10.0)
                .imageUrl("http://image.url/rose.jpg")
                .categoryId(1L)
                .build();

        Product productEntity = Product.builder()
                .id(1L)
                .name("Rose")
                .description("Red flower")
                .price(BigDecimal.valueOf(10.0))
                .imageUrl("http://image.url/rose.jpg")
                .build();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L)
                .name("Rose")
                .description("Red flower")
                .price(10.0)
                .imageUrl("http://image.url/rose.jpg")
                .categoryName("Flowers")
                .build();

        // Мокаем маппер и сервис
        when(productMapper.toEntity(any(ProductRequestDto.class))).thenReturn(productEntity);
        when(productService.create(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Rose",
                                "description": "Red flower",
                                "price": 10.0,
                                "imageUrl": "http://image.url/rose.jpg",
                                "categoryId": 1
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rose"))
                .andExpect(jsonPath("$.categoryName").value("Flowers"));
    }

    // Тест на получение продукта по id
    @Test
    void getById_existingId_returnsProduct() throws Exception {
        Long id = 1L;

        Product product = Product.builder()
                .id(id)
                .name("Rose")
                .description("Red flower")
                .price(BigDecimal.valueOf(10.0))
                .imageUrl("http://image.url/rose.jpg")
                .build();

        ProductResponseDto dto = ProductResponseDto.builder()
                .id(id)
                .name("Rose")
                .description("Red flower")
                .price(10.0)
                .imageUrl("http://image.url/rose.jpg")
                .categoryName("Flowers")
                .build();

        when(productService.getById(id)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(dto);

        mockMvc.perform(get("/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Rose"));
    }

}
