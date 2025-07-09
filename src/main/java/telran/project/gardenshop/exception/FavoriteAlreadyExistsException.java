package telran.project.gardenshop.exception;

public class FavoriteAlreadyExistsException extends RuntimeException {

    public FavoriteAlreadyExistsException(Long userId, Long productId) {
        super("Favorite for user " + userId + " and product " + productId + " already exists");
    }


}
