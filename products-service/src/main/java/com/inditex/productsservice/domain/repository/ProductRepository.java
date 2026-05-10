package com.inditex.productsservice.domain.repository;

import com.inditex.productsservice.domain.model.Product;
import reactor.core.publisher.Mono;
import java.util.Set;

public interface ProductRepository {
    Mono<Set<String>> getSimilarIds(String productId);
    Mono<Product> getProductDetail(String productId);
}