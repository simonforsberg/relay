package org.example.userservice.dto;

import lombok.Getter;

@Getter
public class UserRequest {
    private String username;
    private String password;
    private String email;
}