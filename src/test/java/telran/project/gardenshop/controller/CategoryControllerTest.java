//package telran.project.gardenshop.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.MediaType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.web.servlet.MockMvc;
//import telran.project.gardenshop.dto.CategoryRequestDto;
//import telran.project.gardenshop.dto.CategoryResponseDto;
//import telran.project.gardenshop.service.CategoryService;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CategoryController.class)
//class CategoryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private CategoryService categoryService; // инжектится из TestConfig
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public CategoryService categoryService() {
//            return Mockito.mock(CategoryService.class);
//        }
//    }
//
//    @Test
//    void testCreate() throws Exception {
//        CategoryRequestDto requestDto = new CategoryRequestDto();
//        requestDto.setCategory("Flowers");
//
//        CategoryResponseDto responseDto = new CategoryResponseDto();
//        responseDto.setId(1L);
//        responseDto.setCategory("Flowers");
//
//        when(categoryService.create(any(CategoryRequestDto.class))).thenReturn(responseDto);
//
//        mockMvc.perform(post("/categories")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Flowers"));
//    }
//
//    @Test
//    void testGetAll() throws Exception {
//        CategoryResponseDto responseDto = new CategoryResponseDto();
//        responseDto.setId(1L);
//        responseDto.setCategory("Flowers");
//
//        when(categoryService.getAll()).thenReturn(List.of(responseDto));
//
//        mockMvc.perform(get("/categories"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].name").value("Flowers"));
//    }
//
//    @Test
//    void testGetById() throws Exception {
//        CategoryResponseDto responseDto = new CategoryResponseDto();
//        responseDto.setId(1L);
//        responseDto.setCategory("Flowers");
//
//        when(categoryService.getById(1L)).thenReturn(responseDto);
//
//        mockMvc.perform(get("/categories/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Flowers"));
//    }
//
//    @Test
//    void testDelete() throws Exception {
//        doNothing().when(categoryService).delete(1L);
//
//        mockMvc.perform(delete("/categories/1"))
//                .andExpect(status().isOk());
//    }
//}