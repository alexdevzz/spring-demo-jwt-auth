package com.backend.demojwtauth.app.demo;

import com.backend.demojwtauth.app.common.dto.ApiResponse;
import com.backend.demojwtauth.app.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {


    @GetMapping("/welcome")
    public ResponseEntity<ApiResponse<Map<String, Object>>> welcome(@AuthenticationPrincipal User user) {

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Welcome from secure endpoint");
        data.put("user", user.getUsername());
        data.put("full_name", user.getFirstName() + " " + user.getLastName());
        data.put("country", user.getCountry());
        data.put("role", user.getRole());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Access granted to protected resource",
                        data
                    )
                );
    }

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userInfo(Authentication authentication) {

        Map<String, Object> data = new HashMap<>();
        data.put("username", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        data.put("is_authenticated", authentication.isAuthenticated());

        return ResponseEntity.ok(ApiResponse.success(
                "User authentication details",
                data
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> info() {

        return ResponseEntity.ok(ApiResponse.success(
                "This is a protected endpoint",
                "You are authenticated and authorized to access this resource"
        ));
    }
}