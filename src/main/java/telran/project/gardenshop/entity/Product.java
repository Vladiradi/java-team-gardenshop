package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data // включает @Getter, @Setter, @ToString, @EqualsAndHashCode и @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double price;

    private String imageUrl;

    @ManyToOne
    private Category category;

//     @OneToMany(mappedBy = "product")
//     private List<Favorite> favorites;
}
