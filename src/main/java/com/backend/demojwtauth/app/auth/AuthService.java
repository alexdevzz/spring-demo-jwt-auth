package com.backend.demojwtauth.app.auth;

import com.backend.demojwtauth.app.auth.dto.AuthResponse;
import com.backend.demojwtauth.app.auth.dto.LoginRequest;
import com.backend.demojwtauth.app.auth.dto.RegisterRequest;
import com.backend.demojwtauth.app.common.exception.BusinessException;
import com.backend.demojwtauth.app.jwt.JwtService;
import com.backend.demojwtauth.app.user.Role;
import com.backend.demojwtauth.app.user.User;
import com.backend.demojwtauth.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        // validate if user exist before authenticate
        Optional<User> OptionalUser = userRepository.findByUsername(request.getUsername());
        if (OptionalUser.isEmpty()) {
            throw BusinessException.userNotFound(request.getUsername());
        }

        // try to authenticate (throw BadCredentialsException if fail)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // generate JWT token
        String token = jwtService.getToken(OptionalUser.get());
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // check if user already exist
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw BusinessException.userAlreadyExists(request.getUsername());
        }

        // additional business validations
        validateRegistrationData(request);

        // create new user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .country(request.getCountry())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .build();

        try {
            userRepository.save(user);
        } catch (Exception ex) {
            throw new BusinessException(
                    "Error saving user to database",
                    "DATABASE_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // Generate new JWT Token for register user
        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();

    }

    /**
     * additional business validations
     * @param request RegisterRequest
     */
    private void validateRegistrationData(RegisterRequest request) {
        // min length username
        if (request.getUsername().length() < 3) {
            throw BusinessException.invalidOperation(
                    "register",
                    "Username must be at least 3 characters long"
            );
        }

        // min length password
        if (request.getPassword().length() < 8) {
            throw BusinessException.invalidOperation(
                    "register",
                    "Password must be at least 8 characters long"
            );
        }

        // names not empty
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw BusinessException.invalidOperation(
                    "register",
                    "First name cannot be empty"
            );
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw BusinessException.invalidOperation(
                    "register",
                    "Last name cannot be empty"
            );
        }

        // country not empty
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            throw BusinessException.invalidOperation(
                    "register",
                    "Country cannot be empty"
            );
        }
    }
}
