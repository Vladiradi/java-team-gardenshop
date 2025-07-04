package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;
import telran.project.gardenshop.enums.Role;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;
    private String phoneNumber;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
}
