package com.inditex.productsservice.infrastructure.api;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.infrastructure.api.mapper.ProductExternalMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.infrastructure.api.mapper.ProductExternalMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static java.util.Collections.emptySet;

@ExtendWith(MockitoExtension.class)
class ProductClientTest {
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec headersUriSpec;
    @Mock private WebClient.RequestHeadersSpec headersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private ProductExternalMapper mapper;
    
    @Spy private CircuitBreaker productCircuitBreaker = CircuitBreaker.ofDefaults("test");

    @InjectMocks private ProductClient productClient;

    @Test
    void shouldReturnEmptySetWhenExternalApiFails() {
        when(webClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(anyString(), anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String[].class)).thenReturn(Mono.error(new RuntimeException("Timeout!")));

        Mono<Set<String>> result = productClient.getSimilarIds("1");

        StepVerifier.create(result)
                .expectNext(java.util.Collections.emptySet())
                .verifyComplete();
    }
}