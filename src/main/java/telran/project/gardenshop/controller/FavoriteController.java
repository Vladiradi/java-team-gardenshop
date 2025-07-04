package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.service.FavoriteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponseDto> add(@RequestBody @Valid FavoriteRequestDto dto) {
        Favorite favorite = favoriteService.addToFavorites(dto);

        FavoriteResponseDto response = FavoriteResponseDto.builder()
                .productId(favorite.getProduct().getId())
                .productName(favorite.getProduct().getName())
                .price(favorite.getProduct().getPrice())
                .imageUrl(favorite.getProduct().getImageUrl())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponseDto>> getAllByUser(@PathVariable Long userId) {
        List<FavoriteResponseDto> favorites = favoriteService.getFavoritesByUserId(userId).stream()
                .map(fav -> FavoriteResponseDto.builder()
                        .productId(fav.getProduct().getId())
                        .productName(fav.getProduct().getName())
                        .price(fav.getProduct().getPrice())
                        .imageUrl(fav.getProduct().getImageUrl())
                        .build())
                .toList();

        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestParam Long userId, @RequestParam Long productId) {
        favoriteService.removeFromFavorites(userId, productId);
        return ResponseEntity.noContent().build();
    }
}

