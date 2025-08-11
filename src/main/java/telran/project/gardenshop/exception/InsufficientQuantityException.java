package telran.project.gardenshop.exception;

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
    
    public InsufficientQuantityException(Long productId, int availableQuantity, int requestedQuantity, String message) {
        super(message);
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }
    
    public InsufficientQuantityException(Long productId, int availableQuantity, int requestedQuantity, String message, Throwable cause) {
        super(message, cause);
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }
    
    public int getRequestedQuantity() {
        return requestedQuantity;
    }
} 