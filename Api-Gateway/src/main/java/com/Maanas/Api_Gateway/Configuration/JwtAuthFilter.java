package com.Maanas.Api_Gateway.Configuration;

import com.Maanas.Api_Gateway.Servcies.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter {

    @Autowired
    private JWTUtil jwtUtil;

    public GatewayFilter apply() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("Missing or malformed Authorization header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                System.out.println("Token Claims: " + claims);

                // Check roles
                List<String> roles = claims.get("roles", List.class);
                if (!roles.contains("USER")) {
                    System.out.println("Invalid roles in token: " + roles);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // Pass claims to downstream services
                exchange.getRequest().mutate()
                        .header("X-User-Id", claims.getSubject())
                        .header("X-Roles", String.join(",", roles));

            } catch (JwtException e) {
                System.out.println("Token validation failed: " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }
}
