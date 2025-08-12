package telran.project.gardenshop.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.project.gardenshop.dto.FavoriteRequestDto;
import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.mapper.FavoriteMapper;
import telran.project.gardenshop.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import telran.project.gardenshop.entity.Favorite;

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
        Favorite saved = favoriteService.addToFavorites(dto.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteMapper.toDto(saved));
    }

    @Operation(summary = "Remove product from user's favorites")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        favoriteService.removeFromFavorites(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all favorite products for current user")
    @GetMapping
    public ResponseEntity<List<FavoriteResponseDto>> getAll() {
        return ResponseEntity.ok(
                favoriteService.getAll().stream()
                        .map(favoriteMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @Operation(summary = "Get specific favorite by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FavoriteResponseDto> getById(@PathVariable Long id) {
        Favorite favorite = favoriteService.getFavoriteById(id);
        return ResponseEntity.ok(favoriteMapper.toDto(favorite));
    }
}
