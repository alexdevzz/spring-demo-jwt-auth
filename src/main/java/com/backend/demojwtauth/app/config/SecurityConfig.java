package com.backend.demojwtauth.app.config;

import com.backend.demojwtauth.app.jwt.JwtAuthenticationFiler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFiler jwtAuthenticationFiler;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // deshabilitar csrf
                .csrf(csrf -> csrf.disable())
                // establecer que rutas están protegidas y cuáles no
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                // deshabilitar sesiones
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // añadir el proveedor de authentication
                .authenticationProvider(authProvider)
                // añadir jwtAuthenticationFilter antes de UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFiler, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
