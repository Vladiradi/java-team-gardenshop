package telran.project.gardenshop.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("Category with id " + id + " not found");
    }

    public CategoryNotFoundException(String name) {
        super("Category with name '" + name + "' not found");
    }
}