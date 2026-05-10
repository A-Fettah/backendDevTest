package com.inditex.productsservice.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    @Value("${resilience4j.circuitbreaker.instances.productService.failureRateThreshold:50}")
    private float failureRate;

    @Value("${resilience4j.circuitbreaker.instances.productService.slidingWindowSize:10}")
    private int slidingWindow;

    @Value("${resilience4j.circuitbreaker.instances.productService.waitDurationInOpenState:10s}")
    private String waitDuration;

    @Value("${resilience4j.circuitbreaker.instances.productService.permittedNumberOfCallsInHalfOpenState:3}")
    private int halfOpenCalls;

    @Bean
    public CircuitBreaker productCircuitBreaker() {
        Duration waitInOpen = Duration.parse("PT" + waitDuration.toUpperCase());

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(failureRate)
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(slidingWindow)
            .minimumNumberOfCalls(5)
            .waitDurationInOpenState(waitInOpen)
            .permittedNumberOfCallsInHalfOpenState(halfOpenCalls)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();

        return CircuitBreaker.of("productService", config);
    }
}