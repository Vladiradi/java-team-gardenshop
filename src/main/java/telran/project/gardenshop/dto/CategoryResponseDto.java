package telran.project.gardenshop.dto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
public class CategoryResponseDto {

    @EqualsAndHashCode.Include
    private Long id;
    private String category;
}
