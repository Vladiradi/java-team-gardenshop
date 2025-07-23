package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    private BigDecimal price;
}
//4. OrderItem (Товары в заказе)
//Entity: OrderItem (id, order_id, product_id, quantity, price)
//Создаётся из CartItem при оформлении заказа
//Связь: Order → OrderItems
//Реализация:
//DTO, Repository, используется в OrderService
//5. Переход Cart → Order
//User имеет Cart (один ко многим)
//Cart содержит список CartItems
//Каждая CartItem ссылается на:
//product_id
//quantity
//price
//Пример:
///api/orders → Order → OrderItems
//Пользователь оформляет заказ:
//
//POST /api/orders
//Данные берутся из Cart
//Создаётся объект Order
//
//Из CartItems формируются OrderItems:
//
//переносятся только quantity заказа
//CartItems обновляются или удаляются
//Связи:
//User 1 → 1 Cart
//
//Cart 1 → * CartItems
//
//CartItem * → 1 Product
//
//User 1 → * Orders
//
//Order 1 → * OrderItems
//
//OrderItem * → 1 Product
//
//Итог:
//Добавление товара — создаёт Cart, если нет
//Заказ — переносит часть данных в Order и обновляет Cart
//Всё управляется через API /api/orders
