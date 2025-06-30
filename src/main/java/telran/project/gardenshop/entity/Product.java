package telran.project.gardenshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products")
@Data // включает @Getter, @Setter, @ToString, @EqualsAndHashCode и @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @JsonIgnore
     @OneToMany(mappedBy = "product")
     private List<Favorite> favorites;
}
//Почему mappedBy = "product"
//Это говорит Hibernate: "Не создавай новую таблицу связи!", потому что связь уже описана в сущности Favorite, в поле product.
//
//То есть Product сам не владеет этой связью, просто ссылается обратно.
//
//🧱 Что делает @JsonIgnore
//Когда ты возвращаешь Product как JSON (например, через REST-контроллер), Spring Jackson будет сериализовать все поля. В том числе и favorites.
//
//Но:
//
//favorites — это список объектов Favorite
//
//а Favorite содержит product
//
//который снова содержит favorites
//
//и так по кругу...
//
//💥 Это вызывает бесконечную рекурсию при сериализации → StackOverflowError.
//
//Поэтому @JsonIgnore:
//Говорит: "Не включай это поле в JSON"
//
//Предотвращает бесконечный цикл
//Да, если:
//Ты хочешь потом, например, по продукту узнать, кто добавил его в избранное
//
//Или использовать это для админки, статистики и т.д.
//
//Нет, если:
//Тебе не нужен доступ к этому списку избранного изнутри Product
//
//Ты используешь Favorite отдельно и не ходишь в обратную сторону
//
//Если не нужно — можешь удалить это поле вовсе.
