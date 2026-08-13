package com.techsolutions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad de Spring Security para TechSolutions S.A.
 *
 * <p>Usuarios en memoria para prueba:</p>
 * <ul>
 *   <li>gerente / gerente123  → rol GERENTE  → puede ver reportes</li>
 *   <li>contador / contador123 → rol CONTADOR → puede ver reportes</li>
 *   <li>vendedor / vendedor123 → rol VENDEDOR → NO puede ver reportes</li>
 *   <li>admin / admin123       → rol ADMIN    → gestiona adaptadores de pago</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails gerente = User.builder()
                .username("gerente")
                .password(encoder.encode("gerente123"))
                .roles("GERENTE")
                .build();

        UserDetails contador = User.builder()
                .username("contador")
                .password(encoder.encode("contador123"))
                .roles("CONTADOR")
                .build();

        UserDetails vendedor = User.builder()
                .username("vendedor")
                .password(encoder.encode("vendedor123"))
                .roles("VENDEDOR")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(gerente, contador, vendedor, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/api/payments/**").hasRole("ADMIN")
                .requestMatchers("/api/reports/**").authenticated()
                .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "GERENTE")
                // RF10: solo el administrador puede seleccionar/cambiar la estrategia de precios activa
                .requestMatchers(HttpMethod.PUT, "/api/pricing/strategies/**").hasRole("ADMIN")
                .requestMatchers("/api/pricing/**").authenticated()
                .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "GERENTE", "VENDEDOR", "CONTADOR")
                .requestMatchers("/api/catalog/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
