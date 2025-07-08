package telran.project.gardenshop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/swagger-resources/**",
                                    "/webjars/**"
                            ).permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

//@Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // для REST API CSRF обычно отключают
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(
//                    "/v3/api-docs/**",
//                    "/swagger-ui/**",
//                    "/swagger-ui.html",
//                    "/swagger-resources/**",
//                    "/webjars/**"
//                ).permitAll()
//                .anyRequest().authenticated()  // все остальные запросы требуют аутентификации
//            )
//            .httpBasic(Customizer.withDefaults()); // включаем базовую аутентификацию
//
//        return http.build();
//    }
//
//    // Для примера — простой пользователь в памяти, можно заменить на свою UserDetailsService
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.withUsername("user")
//            .password(passwordEncoder.encode("password"))
//            .roles("USER")
//            .build();
//        return new InMemoryUserDetailsManager(user);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}

//Что это даст?
//Swagger и его ресурсы будут доступны без пароля.
//
//Все остальные запросы требуют базовую аутентификацию (логин/пароль).
//
//Пароль хранится в памяти (просто для теста).
//
//Ты можешь зайти в API через Postman или браузер, указав user / password.
//
//Как дальше?
//Когда будешь готов — заменишь InMemoryUserDetailsManager на свою реализацию с базой данных и JWT.
//
//Добавишь роли и разрешения, чтобы разграничить доступ.
//
//Обновишь конфигурацию безопасности под требования.