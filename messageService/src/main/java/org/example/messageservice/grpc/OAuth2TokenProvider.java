package org.example.messageservice.grpc;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class OAuth2TokenProvider {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public OAuth2TokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken() {
        var authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("authservice")
                .principal("messageService")
                .build();

        var client = authorizedClientManager.authorize(authorizeRequest);
        if (client == null) {
            throw new IllegalStateException("Could not obtain access token from authService");
        }
        return client.getAccessToken().getTokenValue();
    }
}