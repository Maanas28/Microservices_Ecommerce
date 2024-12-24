package com.Maanas.Authentication_Service.Services;

import com.Maanas.Authentication_Service.Model.User;
import com.Maanas.Authentication_Service.Repositorys.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final Clock clock;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, Clock clock) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.clock = clock;
    }

    public Map<String, String> registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setRoles(new HashSet<>(Set.of("USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Map<String, String> tokens = generateTokens(user);
        userRepository.save(user);
        return tokens;
    }

    public Map<String, String> registerAdmin(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setRoles(new HashSet<>(Set.of("ADMIN")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Map<String, String> tokens = generateTokens(user);
        userRepository.save(user);
        return tokens;
    }

    public Map<String, String> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return generateTokens(user);
    }


    private Map<String, String> generateTokens(User user) {
        Map<String, Object> claims = Map.of(
                "roles", user.getRoles(),
                "permissions", getPermissions(user.getRoles())
        );

        String accessToken = jwtUtil.generateToken(user.getUsername(), claims, 15 * 60 * 1000); // 15 mins
        String refreshToken = jwtUtil.generateToken(user.getUsername(), Map.of(), 7 * 24 * 60 * 60 * 1000); // 7 days

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(clock.millis() + 7 * 24 * 60 * 60 * 1000); // 7 days
        userRepository.save(user);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    private List<String> getPermissions(Set<String> roles) {
        Map<String, List<String>> rolePermissions = Map.of(
                "USER", List.of("READ"),
                "ADMIN", List.of("READ", "WRITE", "DELETE")
        );

        return roles.stream()
                .flatMap(role -> {
                    if (!rolePermissions.containsKey(role)) {
                        throw new IllegalArgumentException("Unknown role: " + role);
                    }
                    return rolePermissions.get(role).stream();
                })
                .toList();
    }

    // Refresh Token Logic
    public Map<String, String> refresh(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (System.currentTimeMillis() > user.getRefreshTokenExpiry()) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return generateTokens(user);
    }
}
