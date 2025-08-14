package telran.project.gardenshop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartResponseDto {

    private Long id;

    private Long userId;

    private List<CartItemResponseDto> items = new ArrayList<>();
}
