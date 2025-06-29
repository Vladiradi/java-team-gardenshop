package telran.project.gardenshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long userId,
                                               @PathVariable Long productId) {
        favoriteService.addToFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> addToFavorites(@RequestBody FavoriteRequestDto dto) {
        favoriteService.addToFavorites(dto.getUserId(), dto.getProductId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long userId, @PathVariable Long productId) {
        favoriteService.removeFromFavorites(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponseDto>> getFavorites(@PathVariable Long userId) {
        List<FavoriteResponseDto> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{userId}/{productId}/exists")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        boolean isFav = favoriteService.isFavorite(userId, productId);
        return ResponseEntity.ok(isFav);
    }
}
