package telran.project.gardenshop.entity;

import jakarta.persistence.*;
import lombok.*;
import telran.project.gardenshop.enums.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}