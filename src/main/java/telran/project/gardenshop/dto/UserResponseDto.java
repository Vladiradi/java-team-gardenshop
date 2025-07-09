package telran.project.gardenshop.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private List<FavoriteResponseDto> favorites;
}