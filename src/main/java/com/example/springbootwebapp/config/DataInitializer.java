package com.example.springbootwebapp.config;

import com.example.springbootwebapp.model.Role;
import com.example.springbootwebapp.model.User;
import com.example.springbootwebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            //Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Admin User");
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin user created - username: admin, password: admin123");
            }
            
            //Create regular user if not exists
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setFullName("Regular User");
                user.setRole(Role.USER);
                userRepository.save(user);
                System.out.println("Regular user created - username: user, password: user123");
            }
        };
    }
}
