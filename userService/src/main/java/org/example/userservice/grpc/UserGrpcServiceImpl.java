package org.example.userservice.grpc;

import io.grpc.stub.StreamObserver;
import org.example.userservice.repository.UserRepository;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class UserGrpcServiceImpl extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserRepository userRepository;

    public UserGrpcServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUserByUsername(
            GetUserByUsernameRequest request,
            StreamObserver<UserResponse> responseObserver) {

        userRepository.findByUsername(request.getUsername())
                .ifPresentOrElse(
                        user -> {
                            UserResponse response = UserResponse.newBuilder()
                                    .setId(user.getId().toString())
                                    .setUsername(user.getUsername())
                                    .setEmail(user.getEmail())
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                io.grpc.Status.NOT_FOUND
                                        .withDescription("User not found: " + request.getUsername())
                                        .asRuntimeException()
                        )
                );
    }
}