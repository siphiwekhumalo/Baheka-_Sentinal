package com.baheka.sentinel.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@SpringBootApplication
class SentinelGatewayApplication {

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            // Risk Service Routes
            .route("risk-service") { r ->
                r.path("/api/v1/risk/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Service", "risk")
                    }
                    .uri("http://localhost:8081")
            }
            // AML Service Routes
            .route("aml-service") { r ->
                r.path("/api/v1/aml/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Service", "aml")
                    }
                    .uri("http://localhost:8082")
            }
            // Compliance Service Routes
            .route("compliance-service") { r ->
                r.path("/api/v1/compliance/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Service", "compliance")
                    }
                    .uri("http://localhost:8083")
            }
            // Security Service Routes
            .route("security-service") { r ->
                r.path("/api/v1/security/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Service", "security")
                    }
                    .uri("http://localhost:8084")
            }
            // Notification Service Routes
            .route("notification-service") { r ->
                r.path("/api/v1/notifications/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Service", "notification")
                    }
                    .uri("http://localhost:8085")
            }
            .build()
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
            maxAge = 3600L
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }

        return CorsWebFilter(source)
    }
}

fun main(args: Array<String>) {
    runApplication<SentinelGatewayApplication>(*args)
}
