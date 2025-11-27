package com.backend.demojwtauth.app.auth;

import com.backend.demojwtauth.app.auth.dto.AuthResponse;
import com.backend.demojwtauth.app.auth.dto.LoginRequest;
import com.backend.demojwtauth.app.auth.dto.RegisterRequest;
import com.backend.demojwtauth.app.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint to login
     * @param request user credentials
     * @return JWT Token wrapped in ApiResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);
        ApiResponse<AuthResponse> apiResponse = ApiResponse.success(
                "Login successful",
                authResponse
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    /**
     * Endpoint to register a new user
     * @param request new user data
     * @return JWT Token wrapped in ApiResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);
        ApiResponse<AuthResponse> apiResponse = ApiResponse.success(
                "User registered successful",
                authResponse
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
