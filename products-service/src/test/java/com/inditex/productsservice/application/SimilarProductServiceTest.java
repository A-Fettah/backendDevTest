package com.inditex.productsservice.application.service;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimilarProductServiceTest {

    @Mock private ProductRepository repository;
    @Mock private CacheManager cacheManager;
    @Mock private Cache cache;

    @InjectMocks private SimilarProductService service;

    @Test
    void shouldReturnProductsAndStoreInCache() {
        // GIVEN
        String id = "1";
        Product product = new Product("2", "Test Product", 19.99, true);
        
        when(repository.getSimilarIds(id)).thenReturn(Mono.just(Set.of("2")));
        when(cacheManager.getCache("products")).thenReturn(cache);
        when(cache.get("2", Product.class)).thenReturn(null); 
        when(repository.getProductDetail("2")).thenReturn(Mono.just(product));

        // WHEN
        Flux<Product> result = service.getSimilarProducts(id);

        // THEN
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();

        verify(cache).put("2", product);
    }

    @Test
    void shouldReturnProductFromCacheWithoutCallingRepository() {
        // GIVEN
        String id = "1";
        Product cachedProduct = new Product("2", "Cached", 10.0, true);
        
        when(repository.getSimilarIds(id)).thenReturn(Mono.just(Set.of("2")));
        when(cacheManager.getCache("products")).thenReturn(cache);
        when(cache.get("2", Product.class)).thenReturn(cachedProduct); 

        // WHEN
        Flux<Product> result = service.getSimilarProducts(id);

        // THEN
        StepVerifier.create(result)
                .expectNext(cachedProduct)
                .verifyComplete();

        verify(repository, never()).getProductDetail(anyString()); 
    }
}