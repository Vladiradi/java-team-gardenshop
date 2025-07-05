package telran.project.gardenshop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
@Validated
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteMapper favoriteMapper;

    @PostMapping
    public ResponseEntity<FavoriteResponseDto> add(@RequestBody @Valid FavoriteRequestDto dto) {
        Favorite favorite = favoriteService.addToFavorites(dto);

        FavoriteResponseDto response = favoriteMapper.toDto(favorite);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponseDto>> getAllByUser(@PathVariable Long userId) {
        List<FavoriteResponseDto> favorites = favoriteService.getFavoritesByUserId(userId).stream()
                .map(favoriteMapper::toDto)
                .toList();

        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(
            @RequestParam @NotNull(message = "User ID is required") Long userId,
            @RequestParam @NotNull(message = "Product ID is required") Long productId) {
        favoriteService.removeFromFavorites(userId, productId);
        return ResponseEntity.noContent().build();
    }
}

