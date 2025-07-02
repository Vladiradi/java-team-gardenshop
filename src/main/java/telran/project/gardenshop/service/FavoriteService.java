package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.FavoriteResponseDto;

import java.util.List;

public interface FavoriteService {
    void addToFavorites(Long userId, Long productId);
    void removeFromFavorites(Long userId, Long productId);
    List<FavoriteResponseDto> getFavoritesByUserId(Long userId);
    boolean isFavorite(Long userId, Long productId);
}