package telran.project.gardenshop.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(Long id) {
        super("Favorite with id " + id + " not found");
    }

    public FavoriteNotFoundException(Long userId, Long productId) {
        super("Favorite for user " + userId + " and product " + productId + " not found");
    }

}
