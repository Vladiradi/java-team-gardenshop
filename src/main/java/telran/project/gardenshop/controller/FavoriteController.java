package telran.project.gardenshop.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;

@RestController
@RequestMapping("/v1/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Favorites", description = "User favorites management operations")
public class FavoriteController {

    private final FavoriteService favoriteService;

    private final FavoriteMapper favoriteMapper;

    @Operation(summary = "Add product to favorites", description = "Add a product to current user's favorites list")
    @PostMapping
    public ResponseEntity<FavoriteResponseDto> add(@Valid @RequestBody FavoriteRequestDto dto) {
        Favorite favorite = Favorite.builder()
                .product(Product.builder().id(dto.getProductId()).build())
                .createdAt(LocalDateTime.now())
                .build();

        Favorite saved = favoriteService.addToFavorites(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteMapper.toDto(saved));
    }

    @Operation(summary = "Update favorite", description = "Modify an existing favorite item")
    @PutMapping("/{id}")
    public ResponseEntity<FavoriteResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody FavoriteRequestDto dto) {
        Favorite updated = Favorite.builder()
                .id(id)
                .product(Product.builder().id(dto.getProductId()).build())
                .build();

        Favorite saved = favoriteService.updateFavorite(id, updated);
        return ResponseEntity.ok(favoriteMapper.toDto(saved));
    }

    @Operation(summary = "Remove from favorites", description = "Remove a product from user's favorites list")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user favorites", description = "Retrieve current user's list of favorite products")
    @GetMapping
    public ResponseEntity<List<FavoriteResponseDto>> getCurrentUserFavorites() {
        List<FavoriteResponseDto> favorites = favoriteService.getCurrentUserFavorites().stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favorites);
    }
}
