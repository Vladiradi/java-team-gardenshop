package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь с Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // связь с Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    private Double  price;
}

//Description
//
//Создать сущность OrderItem с полями: id, order_id, product_id, quantity, price
//
//Настроить связи:
//
//Order ↔ OrderItem (OneToMany)
//
//Product ↔ OrderItem (ManyToOne)
//
//Создать OrderItemResponseDto (и при необходимости OrderItemRequestDto)
//
//Включить список OrderItem в OrderResponseDto
//
//Покрыть логику в OrderServiceTest
