package telran.project.gardenshop.exception;

public class ProductNotInCartException extends RuntimeException {

    private final Long productId;

    public ProductNotInCartException(Long productId) {
        super("Product with ID " + productId + " not found in cart");
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
