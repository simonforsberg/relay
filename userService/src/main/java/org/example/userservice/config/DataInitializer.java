package org.example.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsername("demo")) {
                User demo = new User();
                demo.setUsername("demo");
                demo.setEmail("demo@example.com");
                demo.setPassword(new BCryptPasswordEncoder().encode("demo"));
                userRepository.save(demo);
                log.info("Demo user created");
            } else {
                log.info("Demo user already exists");
            }
        };
    }
}