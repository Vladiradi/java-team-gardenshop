package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Product product;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

//Допустим, Favorite — это избранный товар пользователя. Тогда в ней, например:
//
//id (Long)
//
//User (связь ManyToOne)
//
//Product (связь ManyToOne)
//
//createdAt (время добавления в избранное)
//Предположим, что:
//
//Один пользователь может добавить в избранное много товаров.
//
//Один товар может быть в избранном у многих пользователей.
//
//Это связь Many-to-Many, и мы реализуем её через отдельную сущность Favorite.
