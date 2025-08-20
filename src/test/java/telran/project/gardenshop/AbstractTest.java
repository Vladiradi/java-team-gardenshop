package telran.project.gardenshop;

import telran.project.gardenshop.dto.OrderCreateRequestDto;
import telran.project.gardenshop.dto.OrderItemRequestDto;
import telran.project.gardenshop.dto.OrderItemResponseDto;
import telran.project.gardenshop.dto.OrderResponseDto;
import telran.project.gardenshop.dto.OrderShortResponseDto;
import telran.project.gardenshop.dto.*;
import telran.project.gardenshop.entity.*;
import telran.project.gardenshop.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTest {

    protected Authentication authentication;
    protected SecurityContext context;

    protected Category category1;
    protected Category category2;
    protected Category category3;
    protected Category categoryToCreate;
    protected Category categoryCreated;

    protected Product product1;
    protected Product product2;
    protected Product product3;
    protected Product productToCreate;
    protected Product productCreated;

    protected User user1;
    protected User user2;
    protected User userToCreate;
    protected User userCreated;

    protected Favorite favorite1;
    protected Favorite favorite2;
    protected Favorite favoriteToCreate;
    protected Favorite favoriteCreated;

    protected Cart cart1;
    protected Cart cart2;

    protected CartItem cartItem1;
    protected CartItem cartItem2;
    protected CartItem cartItem3;

    protected Order order1;
    protected Order order2;
    protected Order order3;
    protected Order order4;
    protected Order orderToCreate;

    protected OrderItem orderItem1;
    protected OrderItem orderItem2;
    protected OrderItem orderItem3;
    protected OrderItem orderItem4;
    protected OrderItem orderItem5;
    protected OrderItem orderItemToCreate1;
    protected OrderItem orderItemToCreate2;

    protected CategoryResponseDto categoryResponseDto1;
    protected CategoryResponseDto categoryResponseDto2;
    protected CategoryResponseDto categoryResponseDto3;
    protected CategoryRequestDto categoryRequestDto;
    protected CategoryResponseDto categoryResponseCreatedDto;

    protected ProductResponseDto productResponseDto1;
    protected ProductResponseDto productResponseDto2;
    protected ProductResponseDto productResponseDto3;
    protected ProductRequestDto productRequestDto;
    protected ProductResponseDto productResponseCreatedDto;

    protected UserResponseDto userResponseDto1;
    protected UserResponseDto userResponseDto2;
    protected UserRequestDto userRequestDto;
    protected UserResponseDto userResponseCreatedDto;

    protected FavoriteResponseDto favoriteResponseDto1;
    protected FavoriteResponseDto favoriteResponseDto2;
    protected FavoriteResponseDto favoriteResponseCreatedDto;

    protected CartResponseDto cartResponseDto1;
    protected CartResponseDto cartResponseDto2;
    protected CartItemResponseDto cartItemResponseDto1;
    protected CartItemResponseDto cartItemResponseDto2;
    protected CartItemResponseDto cartItemResponseDto3;

    protected OrderResponseDto orderResponseDto1;
    protected OrderResponseDto orderResponseDto2;
    protected OrderResponseDto orderResponseDto3;
    protected OrderResponseDto orderResponseDto4;
    protected OrderCreateRequestDto orderCreateRequestDto;
    protected OrderShortResponseDto orderShortResponseDto1;
    protected OrderShortResponseDto orderShortResponseDto2;
    protected OrderShortResponseDto orderShortResponseDto3;
    protected OrderShortResponseDto orderShortResponseDto4;
    protected OrderItemResponseDto orderItemResponseDto1;
    protected OrderItemResponseDto orderItemResponseDto2;
    protected OrderItemResponseDto orderItemResponseDto3;
    protected OrderItemResponseDto orderItemResponseDto4;

    @BeforeEach
    protected void setUp() {
        initEntities();
        initSecurityContext();
        initProductDtos();
        initCategoryDtos();
        initFavoriteDtos();
        initUserDtos();
        initCartDtos();
        initOrderDtos();
    }

    private void initSecurityContext() {
        authentication = new UsernamePasswordAuthenticationToken(user1.getEmail(), null);
        context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void initEntities() {
        category1 = Category.builder()
                .id(1L)
                .name("Fertilizer")
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Protective products and septic tanks")
                .build();

        category3 = Category.builder()
                .id(3L)
                .name("Tools and equipment")
                .build();

        categoryToCreate = Category.builder()
                .name("Planting material")
                .build();

        categoryCreated = Category.builder()
                .id(4L)
                .name(categoryToCreate.getName())
                .build();

        product1 = Product.builder()
                .id(1L)
                .name("All-Purpose Plant Fertilizer")
                .discountPrice(BigDecimal.valueOf(8.99))
                .price(BigDecimal.valueOf(11.99))
                .category(category1)
                .description("Balanced NPK formula for all types of plants")
                .imageUrl("https://example.com/images/fertilizer_all_purpose.jpg")
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Organic Tomato Feed")
                .discountPrice(BigDecimal.valueOf(9.49))
                .price(BigDecimal.valueOf(13.99))
                .category(category1)
                .description("Organic liquid fertilizer ideal for tomatoes and vegetables")
                .imageUrl("https://example.com/images/fertilizer_tomato_feed.jpg")
                .build();

        product3 = Product.builder()
                .id(3L)
                .name("Slug & Snail Barrier Pellets")
                .discountPrice(BigDecimal.valueOf(5.75))
                .price(BigDecimal.valueOf(7.50))
                .category(category2)
                .description("Pet-safe barrier pellets to protect plants from slugs")
                .imageUrl("https://example.com/images/protection_slug_pellets.jpg")
                .build();

        productToCreate = Product.builder()
                .name("Garden Tool Set (5 pcs)")
                .discountPrice(BigDecimal.valueOf(19.99))
                .price(BigDecimal.valueOf(24.99))
                .category(category3)
                .description("Essential hand tools set for everyday gardening")
                .imageUrl("https://example.com/images/garden_tool_set.jpg")
                .build();

        productCreated = Product.builder()
                .id(4L)
                .name(productToCreate.getName())
                .price(productToCreate.getPrice())
                .discountPrice(productToCreate.getDiscountPrice())
                .category(productToCreate.getCategory())
                .description(productToCreate.getDescription())
                .imageUrl(productToCreate.getImageUrl())
                .build();

        user1 = User.builder()
                .id(1L)
                .fullName("Alice Johnson")
                .email("alice.johnson@example.com")
                .phoneNumber("+1234567890")
                .password("12345")
                .role(Role.USER)
                .build();

        user2 = User.builder()
                .id(2L)
                .fullName("Bob Smith")
                .email("bob.smith@example.com")
                .phoneNumber("+1987654321")
                .password("12345")
                .role(Role.USER)
                .build();

        userToCreate = User.builder()
                .fullName("Charlie Brown")
                .email("charlie.brown@example.com")
                .phoneNumber("+1122334455")
                .password("12345")
                .role(Role.USER)
                .build();

        userCreated = User.builder()
                .id(3L)
                .fullName(userToCreate.getFullName())
                .email(userToCreate.getEmail())
                .phoneNumber(userToCreate.getPhoneNumber())
                .password(userToCreate.getPassword())
                .role(userToCreate.getRole())
                .build();

        favorite1 = Favorite.builder()
                .id(1L)
                .user(user1)
                .product(product1)
                .build();

        favorite2 = Favorite.builder()
                .id(2L)
                .user(user1)
                .product(product2)
                .build();

        favoriteToCreate = Favorite.builder()
                .user(user1)
                .product(product3)
                .build();

        favoriteCreated = Favorite.builder()
                .id(3L)
                .user(favoriteToCreate.getUser())
                .product(favoriteToCreate.getProduct())
                .build();

        cart1 = Cart.builder()
                .id(1L)
                .user(user1)
                .build();

        cartItem1 = CartItem.builder()
                .id(1L)
                .product(product1)
                .quantity(2)
                .build();

        cartItem2 = CartItem.builder()
                .id(2L)
                .product(product2)
                .quantity(1)
                .build();

        cart1.setItems(new ArrayList<>(List.of(cartItem1, cartItem2)));

        cart2 = Cart.builder()
                .id(2L)
                .user(user2)
                .build();

        cartItem3 = CartItem.builder()
                .id(3L)
                .product(product1)
                .quantity(1)
                .build();

        cart2.setItems(new ArrayList<>(List.of(cartItem3)));

        order1 = Order.builder()
                .id(1L)
                .user(user1)
                .deliveryAddress("123 Garden Street")
                .contactName(user1.getFullName())
                .deliveryMethod("COURIER")
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.of(2025, 7, 1, 10, 0, 0))
                .build();

        orderItem1 = OrderItem.builder()
                .id(1L)
                .order(order1)
                .product(product1)
                .quantity(2)
                .price(product1.getDiscountPrice())
                .build();

        orderItem2 = OrderItem.builder()
                .id(2L)
                .order(order1)
                .product(product3)
                .quantity(1)
                .price(product3.getDiscountPrice())
                .build();

        order1.setItems(new ArrayList<>(List.of(orderItem1, orderItem2)));

        order2 = Order.builder()
                .id(2L)
                .user(user2)
                .deliveryAddress("456 Green Ave")
                .contactName(user2.getFullName())
                .deliveryMethod("PICKUP")
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.of(2025, 7, 2, 12, 0, 0))
                .build();

        orderItem3 = OrderItem.builder()
                .id(3L)
                .order(order2)
                .product(product3)
                .quantity(1)
                .price(product3.getDiscountPrice())
                .build();

        order2.setItems(new ArrayList<>(List.of(orderItem3)));

        order3 = Order.builder()
                .id(3L)
                .user(user1)
                .deliveryAddress("123 Garden Street")
                .contactName(user1.getFullName())
                .deliveryMethod("COURIER")
                .status(OrderStatus.DELIVERED)
                .createdAt(LocalDateTime.of(2025, 5, 3, 17, 0, 0))
                .build();

        orderItem4 = OrderItem.builder()
                .id(4L)
                .order(order3)
                .product(product3)
                .quantity(2)
                .price(product3.getDiscountPrice())
                .build();

        order3.setItems(new ArrayList<>(List.of(orderItem4)));

        order4 = Order.builder()
                .id(4L)
                .user(user2)
                .deliveryAddress("456 Green Ave")
                .contactName(user2.getFullName())
                .deliveryMethod("PICKUP")
                .status(OrderStatus.CANCELLED)
                .createdAt(LocalDateTime.of(2025, 7, 1, 11, 45, 0))
                .build();

        orderItem5 = OrderItem.builder()
                .id(5L)
                .order(order4)
                .product(product1)
                .quantity(1)
                .price(product1.getDiscountPrice())
                .build();

        order4.setItems(new ArrayList<>(List.of(orderItem5)));

        orderToCreate = Order.builder()
                .user(user1)
                .deliveryAddress("123 Garden Street")
                .contactName(user1.getFullName())
                .deliveryMethod("COURIER")
                .status(OrderStatus.NEW)
                .build();

        orderItemToCreate1 = OrderItem.builder()
                .product(cartItem1.getProduct())
                .quantity(cartItem1.getQuantity())
                .price(cartItem1.getProduct().getDiscountPrice())
                .build();

        orderItemToCreate2 = OrderItem.builder()
                .product(cartItem2.getProduct())
                .quantity(cartItem2.getQuantity())
                .price(cartItem2.getProduct().getDiscountPrice())
                .build();

        orderToCreate.setItems(new ArrayList<>(List.of(orderItemToCreate1, orderItemToCreate2)));
    }

    private void initCategoryDtos() {
        categoryResponseDto1 = new CategoryResponseDto();
        categoryResponseDto1.setId(category1.getId());
        categoryResponseDto1.setName(category1.getName());

        categoryResponseDto2 = new CategoryResponseDto();
        categoryResponseDto2.setId(category2.getId());
        categoryResponseDto2.setName(category2.getName());

        categoryResponseDto3 = new CategoryResponseDto();
        categoryResponseDto3.setId(category3.getId());
        categoryResponseDto3.setName(category3.getName());

        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName(categoryToCreate.getName());

        categoryResponseCreatedDto = new CategoryResponseDto();
        categoryResponseCreatedDto.setId(categoryCreated.getId());
        categoryResponseCreatedDto.setName(categoryCreated.getName());
    }

    private void initProductDtos() {
        productResponseDto1 = ProductResponseDto.builder()
                .id(product1.getId())
                .name(product1.getName())
                .price(product1.getPrice().doubleValue())
                .discountPrice(product1.getDiscountPrice().doubleValue())
                .categoryName(product1.getCategory().getName())
                .description(product1.getDescription())
                .imageUrl(product1.getImageUrl())
                .build();

        productResponseDto2 = ProductResponseDto.builder()
                .id(product2.getId())
                .name(product2.getName())
                .price(product2.getPrice().doubleValue())
                .discountPrice(product2.getDiscountPrice().doubleValue())
                .categoryName(product2.getCategory().getName())
                .description(product2.getDescription())
                .imageUrl(product2.getImageUrl())
                .build();

        productResponseDto3 = ProductResponseDto.builder()
                .id(product3.getId())
                .name(product3.getName())
                .price(product3.getPrice().doubleValue())
                .discountPrice(product3.getDiscountPrice().doubleValue())
                .categoryName(product3.getCategory().getName())
                .description(product3.getDescription())
                .imageUrl(product3.getImageUrl())
                .build();

        productRequestDto = ProductRequestDto.builder()
                .name(productToCreate.getName())
                .price(productToCreate.getPrice().doubleValue())
                .categoryId(productToCreate.getCategory().getId())
                .description(productToCreate.getDescription())
                .imageUrl(productToCreate.getImageUrl())
                .build();

        productResponseCreatedDto = ProductResponseDto.builder()
                .id(productCreated.getId())
                .name(productCreated.getName())
                .price(productCreated.getPrice().doubleValue())
                .discountPrice(productCreated.getDiscountPrice().doubleValue())
                .categoryName(productCreated.getCategory().getName())
                .description(productCreated.getDescription())
                .imageUrl(productCreated.getImageUrl())
                .build();
    }

    private void initUserDtos() {
        userResponseDto1 = UserResponseDto.builder()
                .id(user1.getId())
                .fullName(user1.getFullName())
                .email(user1.getEmail())
                .phoneNumber(user1.getPhoneNumber())
                .favorites(List.of(favoriteResponseDto1, favoriteResponseDto2))
                .build();

        userResponseDto2 = UserResponseDto.builder()
                .id(user2.getId())
                .fullName(user2.getFullName())
                .email(user2.getEmail())
                .phoneNumber(user2.getPhoneNumber())
                .build();

        userRequestDto = UserRequestDto.builder()
                .fullName(userToCreate.getFullName())
                .email(userToCreate.getEmail())
                .phoneNumber(userToCreate.getPhoneNumber())
                .password(userToCreate.getPassword())
                .build();

        userResponseCreatedDto = UserResponseDto.builder()
                .id(userCreated.getId())
                .fullName(userCreated.getFullName())
                .email(userCreated.getEmail())
                .phoneNumber(userCreated.getPhoneNumber())
                .favorites(new ArrayList<>())
                .build();
    }

    private void initFavoriteDtos() {
        favoriteResponseDto1 = FavoriteResponseDto.builder()
                .id(favorite1.getId())
                .productId(product1.getId())
                .build();

        favoriteResponseDto2 = FavoriteResponseDto.builder()
                .id(favorite2.getId())
                .productId(product2.getId())
                .build();

        favoriteResponseCreatedDto = FavoriteResponseDto.builder()
                .id(favoriteCreated.getId())
                .productId(product3.getId())
                .build();
    }

    private void initCartDtos() {
        cartResponseDto1 = new CartResponseDto();
        cartResponseDto1.setId(cart1.getId());
        cartResponseDto1.setUserId(cart1.getUser().getId());

        cartItemResponseDto1 = CartItemResponseDto.builder()
                .id(cartItem1.getId())
                .productId(product1.getId())
                .quantity(cartItem1.getQuantity())
                .build();

        cartItemResponseDto2 = CartItemResponseDto.builder()
                .id(cartItem2.getId())
                .productId(product2.getId())
                .quantity(cartItem2.getQuantity())
                .build();

        cartResponseDto1.setItems(new ArrayList<>(List.of(cartItemResponseDto1, cartItemResponseDto2)));

        cartResponseDto2 = new CartResponseDto();
        cartResponseDto2.setId(cart2.getId());
        cartResponseDto2.setUserId(cart2.getUser().getId());

        cartItemResponseDto3 = CartItemResponseDto.builder()
                .id(cartItem3.getId())
                .productId(product1.getId())
                .quantity(cartItem3.getQuantity())
                .build();

        cartResponseDto2.setItems(new ArrayList<>(List.of(cartItemResponseDto3)));
    }

    private void initOrderDtos() {
        orderShortResponseDto1 = OrderShortResponseDto.builder()
                .id(order1.getId())
                .status(order1.getStatus().name())
                .deliveryAddress(order1.getDeliveryAddress())
                .contactName(order1.getContactName())
                .deliveryMethod(order1.getDeliveryMethod())
                .build();

        orderShortResponseDto2 = OrderShortResponseDto.builder()
                .id(order2.getId())
                .status(order2.getStatus().name())
                .deliveryAddress(order2.getDeliveryAddress())
                .contactName(order2.getContactName())
                .deliveryMethod(order2.getDeliveryMethod())
                .build();

        orderShortResponseDto3 = OrderShortResponseDto.builder()
                .id(order3.getId())
                .status(order3.getStatus().name())
                .deliveryAddress(order3.getDeliveryAddress())
                .contactName(order3.getContactName())
                .deliveryMethod(order3.getDeliveryMethod())
                .build();

        orderShortResponseDto4 = OrderShortResponseDto.builder()
                .id(order4.getId())
                .status(order4.getStatus().name())
                .deliveryAddress(order4.getDeliveryAddress())
                .contactName(order4.getContactName())
                .deliveryMethod(order4.getDeliveryMethod())
                .build();

        orderResponseDto1 = OrderResponseDto.builder()
                .id(order1.getId())
                .status(order1.getStatus())
                .address(order1.getDeliveryAddress())
                .contactName(order1.getContactName())
                .deliveryMethod(DeliveryMethod.valueOf(order1.getDeliveryMethod()))
                .createdAt(order1.getCreatedAt())
                .build();

        orderItemResponseDto1 = OrderItemResponseDto.builder()
                .id(orderItem1.getId())
                .productId(product1.getId())
                .quantity(orderItem1.getQuantity())
                .price(orderItem1.getPrice().doubleValue())
                .build();

        orderItemResponseDto2 = OrderItemResponseDto.builder()
                .id(orderItem2.getId())
                .productId(product3.getId())
                .quantity(orderItem2.getQuantity())
                .price(orderItem2.getPrice().doubleValue())
                .build();

        orderResponseDto1.setItems(List.of(orderItemResponseDto1, orderItemResponseDto2));

        orderResponseDto2 = OrderResponseDto.builder()
                .id(order2.getId())
                .status(order2.getStatus())
                .address(order2.getDeliveryAddress())
                .contactName(order2.getContactName())
                .deliveryMethod(DeliveryMethod.valueOf(order2.getDeliveryMethod()))
                .createdAt(order2.getCreatedAt())
                .build();

        orderItemResponseDto3 = OrderItemResponseDto.builder()
                .id(orderItem3.getId())
                .productId(product3.getId())
                .quantity(orderItem3.getQuantity())
                .price(orderItem3.getPrice().doubleValue())
                .build();

        orderResponseDto2.setItems(List.of(orderItemResponseDto3));

        orderResponseDto3 = OrderResponseDto.builder()
                .id(order3.getId())
                .status(order3.getStatus())
                .address(order3.getDeliveryAddress())
                .contactName(order3.getContactName())
                .deliveryMethod(DeliveryMethod.valueOf(order3.getDeliveryMethod()))
                .createdAt(order3.getCreatedAt())
                .build();

        orderItemResponseDto4 = OrderItemResponseDto.builder()
                .id(orderItem4.getId())
                .productId(product3.getId())
                .quantity(orderItem4.getQuantity())
                .price(orderItem4.getPrice().doubleValue())
                .build();

        orderResponseDto3.setItems(List.of(orderItemResponseDto4));

        orderCreateRequestDto = OrderCreateRequestDto.builder()
                .deliveryAddress(orderToCreate.getDeliveryAddress())
                .deliveryMethod(DeliveryMethod.COURIER)
                .items(List.of(
                        OrderItemRequestDto.builder()
                                .productId(orderItemToCreate1.getProduct().getId())
                                .quantity(orderItemToCreate1.getQuantity())
                                .build(),
                        OrderItemRequestDto.builder()
                                .productId(orderItemToCreate2.getProduct().getId())
                                .quantity(orderItemToCreate2.getQuantity())
                                .build()))
                .build();

        orderResponseDto4 = OrderResponseDto.builder()
                .id(order4.getId())
                .status(order4.getStatus())
                .address(order4.getDeliveryAddress())
                .contactName(order4.getContactName())
                .deliveryMethod(DeliveryMethod.valueOf(order4.getDeliveryMethod()))
                .createdAt(order4.getCreatedAt())
                .build();
    }
}
