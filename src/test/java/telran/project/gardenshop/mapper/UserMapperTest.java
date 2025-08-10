package telran.project.gardenshop.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.service.security.JwtFilter;
import telran.project.gardenshop.service.security.JwtService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void testToEntity() {
        UserRequestDto dto = UserRequestDto.builder()
                .fullName("john_doe")
                .email("john@example.com")
                .password("password123")
                .build();

        User user = userMapper.toEntity(dto);

        assertThat(user).isNotNull();
        assertThat(user.getFullName()).isEqualTo("john_doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    void testToDtoWithFavorites() {
        Product product = Product.builder()
                .id(1L)
                .name("Rose")
                .price(BigDecimal.valueOf(10.5))
                .imageUrl("rose.jpg")
                .build();

        User userEntity = User.builder()
                .id(2L)
                .fullName("john_doe")
                .email("john@example.com")
                .role(Role.USER)
                .favorites(List.of(Favorite.builder()
                        .id(3L)
                        .user(User.builder().id(2L).build())
                        .product(product)
                        .build()))
                .build();

        UserResponseDto dto = userMapper.toDto(userEntity);

        assertThat(dto).isNotNull();
        assertThat(dto.getFullName()).isEqualTo("john_doe");
        assertThat(dto.getRole()).isEqualTo("USER");

        assertThat(dto.getFavorites()).hasSize(1);
        FavoriteResponseDto favDto = dto.getFavorites().get(0);
        assertThat(favDto.getProductId()).isEqualTo(1L);
        assertThat(favDto.getProductName()).isEqualTo("Rose");
        assertThat(favDto.getPrice()).isEqualTo(10.5);
        assertThat(favDto.getImageUrl()).isEqualTo("rose.jpg");
    }

    @Test
    void testUpdateUserFromDto() {
        User user = User.builder()
                .id(1L)
                .fullName("old_name")
                .email("old@example.com")
                .build();

        UserEditDto editDto = UserEditDto.builder()
                .fullName("new_name")
                .email("new@example.com")
                .build();

        userMapper.updateUserFromDto(editDto, user);

        assertThat(user.getFullName()).isEqualTo("new_name");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }
}
