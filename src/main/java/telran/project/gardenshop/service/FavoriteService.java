package telran.project.gardenshop.service;

import telran.project.gardenshop.entity.Favorite;

import java.util.List;

public interface FavoriteService {

    Favorite addToFavorites(Favorite favorite);

    void removeFromFavorites(Long productId, Long userId);

    List<Favorite> getAllByUserId(Long userId);
}