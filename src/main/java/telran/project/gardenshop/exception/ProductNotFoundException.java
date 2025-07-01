package telran.project.gardenshop.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }

    public ProductNotFoundException(String name) {
        super("Product with name '" + name + "' not found");
    }

}
