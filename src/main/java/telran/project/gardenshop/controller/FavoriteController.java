package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    private final FavoriteMapper favoriteMapper;

    @Operation(summary = "Add product to user's favorites")
    @PostMapping
    public ResponseEntity<FavoriteResponseDto> add(@Valid @RequestBody FavoriteRequestDto dto) {
        Favorite favorite = Favorite.builder()
                .user(User.builder().id(dto.getUserId()).build())
                .product(Product.builder().id(dto.getProductId()).build())
                .createdAt(LocalDateTime.now())
                .build();

        Favorite saved = favoriteService.addToFavorites(favorite);
        return ResponseEntity.status(201).body(favoriteMapper.toDto(saved));
    }

    @Operation(summary = "Remove product from user's favorites")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all favorite products by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteResponseDto>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(
                favoriteService.getAllByUserId(userId).stream()
                        .map(favoriteMapper::toDto)
                        .collect(Collectors.toList()));
    }
}
