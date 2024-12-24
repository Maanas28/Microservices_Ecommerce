package com.Maanas.Authentication_Service.Controller;


import com.Maanas.Authentication_Service.Model.User;
import com.Maanas.Authentication_Service.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Public User Registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
            Map<String, String> response = authService.registerUser(user);
        return ResponseEntity.ok(response);
    }

    // Admin Registration (Protected Endpoint)
    @PostMapping("/admin/register")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody User user) {
        return ResponseEntity.ok(authService.registerAdmin(user));
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(authService.login(username, password));
    }

    // Refresh Token Endpoint
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
