package org.example.authservice.client;

import lombok.extern.slf4j.Slf4j;
import org.example.authservice.dto.UserAuthResponse;
import org.example.authservice.exception.UserServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${userservice.internal.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Optional<UserAuthResponse> findByUsername(String username) {
        try {
            UserAuthResponse response = restClient.get()
                    .uri("/api/users/by-username?username={username}", username)
                    .retrieve()
                    .body(UserAuthResponse.class);
            return Optional.ofNullable(response);
        } catch (HttpClientErrorException.NotFound e) {
            log.debug("User '{}' not found in userService", username);
            return Optional.empty();
        } catch (ResourceAccessException e) {
            log.error("Timeout or network error fetching user '{}': {}", username, e.getMessage());
            throw new UserServiceUnavailableException("userService is unavailable", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching user '{}': {}", username, e.getMessage());
            throw new UserServiceUnavailableException("Unexpected error from userService", e);
        }
    }
}