package com.backend.demojwtauth.app.config.security;

import com.backend.demojwtauth.app.jwt.JwtAuthenticationFiler;
import com.backend.demojwtauth.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFiler jwtAuthenticationFiler;
    private final UserRepository userRepository;
    private final FilterExceptionHandler filterExceptionHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {

        return http
                // disable csrf
                .csrf(csrf -> csrf.disable())
                // set protected routes and public routes
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("auth/register").hasAuthority("ADMIN")
                                .anyRequest().authenticated()
                )
                // config custom handlers for authentication errors and authorization
                .exceptionHandling(exceptionHandler -> exceptionHandler
                        .authenticationEntryPoint(customAuthenticationEntryPoint)           // 401 Unauthenticated
                        .accessDeniedHandler(customAccessDeniedHandler)                     // 403 Unauthorized (Forbidden)
                )
                // disable sessions
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filterExceptionHandler, SecurityContextHolderFilter.class)
                .addFilterBefore(jwtAuthenticationFiler, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
