package org.example.authservice.config;

import org.example.authservice.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class TokenCustomizerConfig {

    private static final Logger log = LoggerFactory.getLogger(TokenCustomizerConfig.class);

    private final UserServiceClient userServiceClient;

    public TokenCustomizerConfig(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            String username = context.getPrincipal().getName();

            userServiceClient.findByUsername(username).ifPresent(user -> {
                context.getClaims().subject(user.id().toString());
                context.getClaims().claim("username", user.username());
                log.debug("JWT customized: sub={}, username={}", user.id(), user.username());
            });
        };
    }
}