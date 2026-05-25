package org.example.authservice.client;

import org.example.authservice.dto.UserAuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

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
            return Optional.empty();
        }
    }
}