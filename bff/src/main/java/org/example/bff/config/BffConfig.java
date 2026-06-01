package org.example.bff.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
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
        return http
                .authorizeHttpRequests(this::configureAuthorization)
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/app.html", true))
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutHandler()))
                .exceptionHandling(this::configureExceptionHandling)
                .build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer
                                                <HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/register.html", "/css/style.css").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // Registrering kräver ingen inloggning
                .anyRequest().authenticated();
    }

    private void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> ex) {
        // Webbläsare skickar "Accept: text/html", skicka till startsidan för inloggning
        ex.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/"),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        );
        // API-klienter får 401
        ex.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                request -> true
        );
    }

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutHandler() {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri(postLogoutRedirectUri);
        return handler;
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