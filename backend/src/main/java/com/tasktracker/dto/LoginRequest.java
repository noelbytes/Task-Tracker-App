package com.tasktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request containing username and password")
public class LoginRequest {
    @Schema(description = "Username", example = "demo", required = true)
    private String username;

    @Schema(description = "Password", example = "demo123", required = true)
    private String password;
}

