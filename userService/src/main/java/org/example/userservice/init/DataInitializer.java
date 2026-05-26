package org.example.userservice.init;

import lombok.extern.slf4j.Slf4j;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedDemoUser();
    }

    private void seedDemoUser() {
        if (userRepository.existsByUsername("demo")) {
            log.debug("Demo user already exists");
            return;
        }
        User demo = new User();
        demo.setUsername("demo");
        demo.setPassword(passwordEncoder.encode("demo"));
        demo.setEmail("demo@example.com");
        userRepository.save(demo);
        log.info("Demo user created: {}", demo.getUsername());
    }
}