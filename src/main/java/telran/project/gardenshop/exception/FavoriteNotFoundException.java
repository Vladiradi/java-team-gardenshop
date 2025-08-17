package telran.project.gardenshop.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(Long id) {
        super("Favorite with id " + id + " not found");
    }
}
