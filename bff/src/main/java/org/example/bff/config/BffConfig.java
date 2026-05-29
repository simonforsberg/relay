package org.example.bff.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class BffConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Value("${bff.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Value("${services.user-service.base-url}")
    private String userServiceUrl;

    @Value("${services.message-service.base-url}")
    private String messageServiceUrl;

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri(postLogoutRedirectUri);

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/register.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/app.html", true))
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler))
                // Omdirigera ej inloggade användare till välkomstsidan istället för OAuth2-flödet
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")))
                .build();
    }

    // Registrering sker utan token. Egen route utan tokenRelay
    @Bean
    public RouterFunction<ServerResponse> userRegistrationRoute() {
        return route()
                .POST("/api/users", http())
                .before(uri(userServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> usersRoute() {
        return route()
                .path("/api/users", builder -> builder
                        .GET("/**", http())
                        .PUT("/**", http())
                        .DELETE("/**", http())
                )
                .before(uri(userServiceUrl))
                .filter(tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> messagesRoute() {
        return route()
                .path("/api/messages", builder -> builder
                        .GET("/**", http())
                        .POST("/**", http())
                )
                .before(uri(messageServiceUrl))
                .filter(tokenRelay())
                .build();
    }
}