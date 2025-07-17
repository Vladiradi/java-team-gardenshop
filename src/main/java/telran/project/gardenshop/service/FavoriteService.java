package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Favorite;

import java.util.List;

public interface FavoriteService {

    Favorite addToFavorites(Favorite favorite);

    void removeFromFavorites(Long id);

    List<Favorite> getAllByUserId(Long userId);

    Favorite getFavoriteById(Long id);

    Favorite updateFavorite(Long id, Favorite updatedFavorite);
}