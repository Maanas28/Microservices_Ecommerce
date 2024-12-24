package com.Maanas.Api_Gateway.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("http://localhost:8080"))
                .route("order-service", r -> r.path("/order/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply()))
                        .uri("http://localhost:8082"))
                .route("cart-service", r -> r.path("/cart/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply()))
                        .uri("http://localhost:8083"))
                .build();
    }
}
