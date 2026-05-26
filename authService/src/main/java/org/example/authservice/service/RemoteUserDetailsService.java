package org.example.authservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.authservice.client.UserServiceClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RemoteUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    public RemoteUserDetailsService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Fetching user '{}' from userService", username);

        return userServiceClient.findByUsername(username)
                .map(u -> User.builder()
                        .username(u.username())
                        .password(u.passwordHash())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> {
                    log.warn("User '{}' not found in userService", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }
}