package telran.project.gardenshop.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import telran.project.gardenshop.dto.CategoryRequestDto;
import telran.project.gardenshop.dto.CategoryResponseDto;
import telran.project.gardenshop.entity.Category;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-01T18:56:31+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (BellSoft)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toEntity(CategoryRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.category( dto.getCategory() );

        return category.build();
    }

    @Override
    public CategoryResponseDto toDto(Category entity) {
        if ( entity == null ) {
            return null;
        }

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();

        categoryResponseDto.setId( entity.getId() );
        categoryResponseDto.setCategory( entity.getCategory() );

        return categoryResponseDto;
    }
}
