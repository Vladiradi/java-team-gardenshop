package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.*;
import telran.project.gardenshop.enums.PaymentMethod;
import telran.project.gardenshop.enums.PaymentStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMethod method;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

}