package com.inditex.productsservice.infrastructure.api;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.domain.repository.ProductRepository;
import com.inditex.productsservice.infrastructure.api.dto.ProductExternalDto;
import com.inditex.productsservice.infrastructure.api.mapper.ProductExternalMapper;
import com.inditex.productsservice.infrastructure.exception.ProductNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClient implements ProductRepository {

    private final WebClient webClient;
    private final ProductExternalMapper mapper;
    private final CircuitBreaker productCircuitBreaker; 

    @Override
    public Mono<Set<String>> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                    Mono.error(new ProductNotFoundException("[PRODUCT CLIENT] Similar IDs not found for: " + productId)))
                .bodyToMono(String[].class)
                .map(ids -> Arrays.stream(ids).collect(Collectors.toSet()))
                .transformDeferred(CircuitBreakerOperator.of(productCircuitBreaker))
                .onErrorResume(t -> fallbackIds(productId, t));
    }

    @Override
    public Mono<Product> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                    Mono.error(new ProductNotFoundException("[PRODUCT CLIENT] Product detail not found for: " + productId)))
                .bodyToMono(ProductExternalDto.class)
                .map(mapper::toDomain)
                .transformDeferred(CircuitBreakerOperator.of(productCircuitBreaker))
                .onErrorResume(t -> fallbackProduct(productId, t));
    }

    private Mono<Set<String>> fallbackIds(String id, Throwable t) {
        log.error("[PRODUCT CLIENT] FallBack activated for similar IDs of {}: {}", id, t.getMessage());
        return Mono.just(Collections.emptySet());
    }

    private Mono<Product> fallbackProduct(String id, Throwable t) {
        log.error("[PRODUCT CLIENT] Fallback activated for product detail of {}: {}", id, t.getMessage());
        return Mono.empty();
    }
}