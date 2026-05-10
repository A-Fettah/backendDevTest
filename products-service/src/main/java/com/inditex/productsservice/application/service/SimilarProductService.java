package com.inditex.productsservice.application.service;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarProductService {

    private final ProductRepository repository;
    private final CacheManager cacheManager;

    public Flux<Product> getSimilarProducts(String productId) {
        log.info("[SERVICE] Retrieving similar products for ID: {}", productId);
        return repository.getSimilarIds(productId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::getDetailWithCache);
    }

    private Mono<Product> getDetailWithCache(String id) {
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            Product cached = cache.get(id, Product.class);
            if (cached != null) {
                log.info("[CACHE] Product {} retrieved from cache", id);
                return Mono.just(cached);
            }
        }

        log.info("[CACHE] Product {} not in cache, querying repository", id);
        return repository.getProductDetail(id)
                .doOnNext(product -> {
                    if (cache != null && product != null) {
                        cache.put(id, product);
                    }
                });
    }
}