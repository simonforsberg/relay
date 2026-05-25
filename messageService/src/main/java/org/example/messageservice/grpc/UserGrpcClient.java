package org.example.messageservice.grpc;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserGrpcClient {

    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final UserGrpcServiceGrpc.UserGrpcServiceBlockingStub stub;
    private final OAuth2TokenProvider tokenProvider;

    public UserGrpcClient(GrpcChannelFactory channelFactory, OAuth2TokenProvider tokenProvider) {
        this.stub = UserGrpcServiceGrpc.newBlockingStub(
                channelFactory.createChannel("userService")
        );
        this.tokenProvider = tokenProvider;
    }

    public Optional<UserResponse> getUserByUsername(String username) {
        try {
            // Hämta JWT och lägg till i request-metadata
            String token = tokenProvider.getAccessToken();
            Metadata headers = new Metadata();
            headers.put(AUTHORIZATION_KEY, "Bearer " + token);

            var stubWithAuth = stub.withInterceptors(
                    MetadataUtils.newAttachHeadersInterceptor(headers)
            );
            GetUserByUsernameRequest request = GetUserByUsernameRequest.newBuilder()
                    .setUsername(username)
                    .build();
            UserResponse response = stubWithAuth
                    .withDeadlineAfter(2, TimeUnit.SECONDS)
                    .getUserByUsername(request);
            log.info("gRPC getUserByUsername({}) → {}", username, response.getUsername());
            return Optional.of(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return Optional.empty();
            }
            log.error("gRPC getUserByUsername({}) failed: {}", username, e.getStatus(), e);
            throw e;
        }
    }
}