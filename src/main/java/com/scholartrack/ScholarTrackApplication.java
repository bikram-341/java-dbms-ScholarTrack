package com.scholartrack;

import com.scholartrack.model.Role;
import com.scholartrack.model.RoleType;
import com.scholartrack.model.User;
import com.scholartrack.repo.RoleRepository;
import com.scholartrack.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableWebSecurity
public class ScholarTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScholarTrackApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/**",
                    "/index.html",
                    "/index.css",
                    "/index.js",
                    "/static/**",
                    "/h2-console/**",
                    "/api/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            for (RoleType type : RoleType.values()) {
                roleRepository.findByName(type)
                        .orElseGet(() -> roleRepository.save(new Role(type)));
            }

            if (!userRepository.existsByUsername("admin")) {
                Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN).orElse(null);
                User admin = new User();
                admin.setUsername("admin");
                admin.setFullName("System Administrator");
                admin.setEmail("admin@scholartrack.edu");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(adminRole);
                admin.setPhone("+91 9876543210");
                userRepository.save(admin);
            }

            System.out.println("\n" + "=".repeat(72));
            System.out.println("  🎓 SCHOLARTRACK - SCHOLARSHIP & ELIGIBILITY TRACKER BACKEND IS LIVE!");
            System.out.println("=".repeat(72));
            System.out.println("  👉 OPEN SCHOLARTRACK WEB PORTAL IN YOUR BROWSER:");
            System.out.println("     http://localhost:8080/");
            System.out.println();
            System.out.println("  👉 INTERACTIVE SWAGGER 3 API DOCUMENTATION:");
            System.out.println("     http://localhost:8080/swagger-ui.html");
            System.out.println();
            System.out.println("  👉 IN-MEMORY H2 DATABASE WEB CONSOLE:");
            System.out.println("     http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:scholartrack_db)");
            System.out.println("=".repeat(72) + "\n");
        };
    }
}
