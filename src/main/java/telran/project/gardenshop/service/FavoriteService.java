package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Favorite;
import java.util.List;

public interface FavoriteService {

    Favorite addToFavorites(Long productId);

    void removeFromFavorites(Long id);

    List<Favorite> getAll();

    Favorite getFavoriteById(Long id);
}
