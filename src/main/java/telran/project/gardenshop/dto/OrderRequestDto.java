package telran.project.gardenshop.dto;

import java.util.List;

public class OrderRequestDto {
    private List<OrderItemRequest> items;

    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }
}
