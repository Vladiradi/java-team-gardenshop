package telran.project.gardenshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEditDto {
    private String title;
    private String description;
    private Double price;
}
