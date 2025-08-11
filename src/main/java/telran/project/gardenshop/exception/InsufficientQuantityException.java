package telran.project.gardenshop.exception;

import lombok.Getter;

@Getter
public class InsufficientQuantityException extends RuntimeException {

    private final Long productId;
    private final int availableQuantity;
    private final int requestedQuantity;

    public InsufficientQuantityException(Long productId, int availableQuantity, int requestedQuantity) {
        super(String.format("Insufficient quantity for product %d. Available: %d, Requested: %d",
                productId, availableQuantity, requestedQuantity));
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }

}
