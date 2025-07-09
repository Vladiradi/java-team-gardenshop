//package telran.project.gardenshop.service;
//
//import telran.project.gardenshop.dto.CategoryRequestDto;
//import telran.project.gardenshop.dto.CategoryResponseDto;
//import telran.project.gardenshop.entity.Category;
//import telran.project.gardenshop.exception.CategoryNotFoundException;
//import telran.project.gardenshop.mapper.CategoryMapper;
//import telran.project.gardenshop.repository.CategoryRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import jakarta.persistence.EntityNotFoundException;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
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
//    private Category category;
//    private CategoryRequestDto requestDto;
//    private CategoryResponseDto responseDto;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        category = new Category();
//        category.setId(1L);
//        category.setCategory("Flowers");
//
//        requestDto = new CategoryRequestDto();
//        requestDto.setCategory("Flowers");
//
//        responseDto = new CategoryResponseDto();
//        responseDto.setId(1L);
//        responseDto.setCategory("Flowers");
//    }
//
//    @Test
//    void testCreate() {
//        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
//        when(categoryRepository.save(category)).thenReturn(category);
//        when(categoryMapper.toDto(category)).thenReturn(responseDto);
//
//        CategoryResponseDto result = categoryService.create(requestDto);
//
//        assertEquals(responseDto, result);
//        verify(categoryRepository).save(category);
//    }
//
//    @Test
//    void testGetAll() {
//        List<Category> categories = List.of(category);
//        when(categoryRepository.findAll()).thenReturn(categories);
//        when(categoryMapper.toDto(category)).thenReturn(responseDto);
//
//        List<CategoryResponseDto> result = categoryService.getAll();
//
//        assertEquals(1, result.size());
//        assertEquals(responseDto, result.get(0));
//    }
//
//    @Test
//    void testGetById_Found() {
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        when(categoryMapper.toDto(category)).thenReturn(responseDto);
//
//        CategoryResponseDto result = categoryService.getById(1L);
//
//        assertEquals(responseDto, result);
//    }
//
//    @Test
//    void testGetById_NotFound() {
//        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(1L));
//    }
//
//    @Test
//    void testDelete_Success() {
//        when(categoryRepository.existsById(1L)).thenReturn(true);
//        doNothing().when(categoryRepository).deleteById(1L);
//
//        categoryService.delete(1L);
//
//        verify(categoryRepository).deleteById(1L);
//    }
//
//    @Test
//    void testDelete_NotFound() {
//        when(categoryRepository.existsById(1L)).thenReturn(false);
//
//        assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(1L));
//    }
//}