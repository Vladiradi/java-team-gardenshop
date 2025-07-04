package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;

import java.util.List;

public interface FavoriteService {
    Favorite addToFavorites(FavoriteRequestDto dto);
    List<Favorite> getFavoritesByUserId(Long userId);
    void removeFromFavorites(Long userId, Long productId);
}