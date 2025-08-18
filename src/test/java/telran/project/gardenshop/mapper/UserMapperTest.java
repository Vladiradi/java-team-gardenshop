package telran.project.gardenshop.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.project.gardenshop.AbstractTest;

import telran.project.gardenshop.dto.FavoriteResponseDto;
import telran.project.gardenshop.dto.UserEditDto;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.Favorite;
import telran.project.gardenshop.entity.Product;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest extends AbstractTest {

        private final UserMapper userMapper = new UserMapperImpl();
        private final FavoriteMapper favoriteMapper = new FavoriteMapperImpl();

        @Test
        void testToEntity() {
                UserRequestDto dto = UserRequestDto.builder()
                                .email("test@example.com")
                                .fullName("Test User")
                                .phoneNumber("+1234567890")
                                .password("password123")
                                .build();

                User user = userMapper.toEntity(dto);

                assertThat(user).isNotNull();
                assertThat(user.getEmail()).isEqualTo("test@example.com");
                assertThat(user.getFullName()).isEqualTo("Test User");
                assertThat(user.getPhoneNumber()).isEqualTo("+1234567890");
                assertThat(user.getPassword()).isEqualTo("password123");
                assertThat(user.getRole()).isEqualTo(Role.USER);
        }

        @Test
        void testToDto() {
                User user = User.builder()
                                .id(1L)
                                .email("test@example.com")
                                .fullName("Test User")
                                .phoneNumber("+1234567890")
                                .role(Role.USER)
                                .build();

                UserResponseDto dto = userMapper.toDto(user);

                assertThat(dto).isNotNull();
                assertThat(dto.getId()).isEqualTo(1L);
                assertThat(dto.getEmail()).isEqualTo("test@example.com");
                assertThat(dto.getFullName()).isEqualTo("Test User");
                assertThat(dto.getPhoneNumber()).isEqualTo("+1234567890");
        }

        @Test
        void testToDtoWithFavorites() {
                Product product = Product.builder()
                                .id(1L)
                                .name("Test Product")
                                .build();

                Favorite favorite = Favorite.builder()
                                .id(1L)
                                .product(product)
                                .build();

                User user = User.builder()
                                .id(1L)
                                .email("test@example.com")
                                .fullName("Test User")
                                .phoneNumber("+1234567890")
                                .role(Role.USER)
                                .favorites(List.of(favorite))
                                .build();

                UserResponseDto dto = userMapper.toDto(user);

                assertThat(dto).isNotNull();
                assertThat(dto.getId()).isEqualTo(1L);
                assertThat(dto.getEmail()).isEqualTo("test@example.com");
                assertThat(dto.getFullName()).isEqualTo("Test User");
                assertThat(dto.getPhoneNumber()).isEqualTo("+1234567890");
                assertThat(dto.getFavorites()).hasSize(1);
                assertThat(dto.getFavorites().get(0).getProductId()).isEqualTo(1L);
        }

        @Test
        void testUpdateUserFromDto() {
                User user = User.builder()
                                .id(1L)
                                .email("old@example.com")
                                .fullName("Old Name")
                                .phoneNumber("+1234567890")
                                .password("oldpass")
                                .role(Role.USER)
                                .build();

                UserEditDto editDto = UserEditDto.builder()
                                .email("new@example.com")
                                .fullName("New Name")
                                .phoneNumber("+9876543210")
                                .password("newpass")
                                .build();

                userMapper.updateUserFromDto(editDto, user);

                assertThat(user.getEmail()).isEqualTo("new@example.com");
                assertThat(user.getFullName()).isEqualTo("New Name");
                assertThat(user.getPhoneNumber()).isEqualTo("+9876543210");
                assertThat(user.getPassword()).isEqualTo("newpass");
                assertThat(user.getId()).isEqualTo(1L);
                assertThat(user.getRole()).isEqualTo(Role.USER);
        }
}
